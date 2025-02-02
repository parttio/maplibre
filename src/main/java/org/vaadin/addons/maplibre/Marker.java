package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.function.SerializableSupplier;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.vaadin.addons.maplibre.dto.SymbolLayout;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Marker extends GeometryLayer {


    private Popover popover;
    private List<String> listeners;
    private SerializableSupplier<Component> popoverContentSupplier;

    protected Marker(MapLibre map, String id, Coordinate coordinate) {
        super(map, id, new GeometryFactory().createPoint(coordinate));
    }

    public Marker withPopup(String html) {
        map.js("""
                    const marker = component.markers['$id'];
                    const popup = new maplibregl.Popup({closeButton: true, closeOnClick: true})
                        .setLngLat(marker.getLngLat())
                        .setHTML('$html');
                    marker.setPopup(popup);
                """, Map.of("id", id, "html", html));
        return this;
    }

    public void setPoint(Point point) {
        this.geometry = point;
        map.js("""
                    const marker = component.markers['$id'];
                    marker.setLngLat(new maplibregl.LngLat($x, $y));
                """, Map.of("id", id, "x", point.getX(), "y", point.getY()));
    }

    public Marker openPopup() {
        map.js("""
                const marker = component.markers['$id'];
                marker.getPopup().addTo(map);
                """, Map.of("id", id));
        return this;
    }

    public Popover setPopover(SerializableSupplier<Component> contentSupplier) {
        this.popoverContentSupplier = contentSupplier;
        if (popover == null) {
            popover = new Popover();
            popover.addThemeVariants(PopoverVariant.ARROW);
            addClickListener(() -> {
                openPopover(popoverContentSupplier);
            });
            popover.addOpenedChangeListener(e -> {
                if (!e.isOpened() && e.isFromClient()) {
                    popover.removeAll();
                    popover.removeFromParent();
                    popover.setFor(id);
                }
            });
        } else {
            popover.removeAll();
            popover.add(popoverContentSupplier.get());
        }
        return popover;
    }

    private void openPopover(SerializableSupplier<Component> contentSupplier) {
        if (popover.getChildren().count() == 0) {
            popover.add(contentSupplier.get());
        }
        if (!popover.getParent().isPresent()) {
            // Apparently Popover needs to be somewhere in the dom to be able to open with id
            map.getElement().appendChild(popover.getElement());
        }
        popover.setFor(id);
        popover.open();
    }

    public void openPopover() {
        openPopover(popoverContentSupplier);
    }

    public Marker addDragEndListener(DragEndListener listener) {
        map.js("""
                    const marker = component.markers['$id'];
                    marker.on('dragend', e => {
                        const lngLat = marker.getLngLat();
                        const evt = new Event("de-$id");
                        evt.lat = lngLat.lat;
                        evt.lng = lngLat.lng;
                        component.dispatchEvent(evt);
                    });
                    marker.setDraggable(true);
                """, Map.of("id", id));
        map.getElement().addEventListener("de-" + id, e -> {
                    double lat = e.getEventData().getNumber("event.lat");
                    double lng = e.getEventData().getNumber("event.lng");
                    Coordinate coordinate = new Coordinate(lng, lat);
                    listener.dragEnd(coordinate);
                }).addEventData("event.lat")
                .addEventData("event.lng");
        return this;
    }

    public void addClickListener(ClickListener l) {
        String cbId = map.registerJsCallback(() -> l.onClick());
        map.js("""
                    const marker = component.markers['$id'];
                    const cbId = '$cbId';
                    marker.getElement().addEventListener("click", e => {
                        e.stopPropagation();
                        component.$server.jsCallback(cbId);
                    });
                """, Map.of("id", id, "cbId", cbId));
        if (listeners == null) {
            listeners = new LinkedList<>();
        }
        listeners.add(cbId);
    }

    @Override
    public void remove() {
        super.remove();
        if (listeners != null) {
            listeners.forEach(map::deregisterJsCallback);
        }
    }

    public void setColor(String color) {
        map.js("""
                    const marker = component.markers['$id'];
                    const element = marker.getElement();
                    const svg = element.getElementsByTagName("svg")[0];
                    const path = svg.getElementsByTagName("path")[0];
                    path.setAttribute("fill", "$color");
                """, Map.of("id", id, "color", color));

    }

    public void setHtml(String rawHtml) {
        rawHtml = rawHtml.replaceAll("\n", "");
        map.js("""
                    const marker = component.markers['$id'];
                    const element = marker.getElement();
                    element.innerHTML = '$html';
                """, Map.of("id", id, "html", rawHtml));
    }

    public void setOffset(double x, double y) {
        map.js("""
                    const marker = component.markers['$id'];
                    debugger;
                    marker.setOffset([$x, $y]);
                """, Map.of("id", id, "x", x, "y", y));
    }

    public void setRotation(int rotationInDegrees) {
        map.js("""
                    const marker = component.markers['$id'];
                    marker.setRotation($rotation);
                """, Map.of("id", id, "rotation", rotationInDegrees));
    }

    public void setRotationAlignment(SymbolLayout.RotationAlignment rotationAlignment) {
        map.js("""
                    const marker = component.markers['$id'];
                    marker.setRotationAlignment('$rotationAlignment');
                """, Map.of("id", id, "rotationAlignment", rotationAlignment.toString()));
    }

    public interface DragEndListener {
        void dragEnd(Coordinate coordinate);
    }

    public interface ClickListener {

        void onClick();
    }
}
