package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Marker extends Layer {


    Marker(MapLibre map, String id, Coordinate coordinate) {
        super(map, id,new GeometryFactory().createPoint(coordinate));
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

    public void openPopup() {
        map.js("""
            const marker = component.markers['$id'];
            marker.getPopup().addTo(map);
            """, Map.of("id", id));
    }

    public interface DragEndListener {
        void dragEnd(Coordinate coordinate);
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

    public interface ClickListener {

        void onClick();
    }

    private List<String> listeners;

    public void addClickListener(ClickListener l) {
        String cbId = map.registerJsCallback(() -> l.onClick());
        map.js("""
            const marker = component.markers['$id'];
            const cbId = '$cbId';
            marker.getElement().addEventListener("click", e => {
                component.$server.jsCallback(cbId);
            });
        """, Map.of("id", id, "cbId", cbId));
        if(listeners == null) {
            listeners = new LinkedList<>();
        }
        listeners.add(cbId);
    }

    @Override
    public void remove() {
        super.remove();
        if(listeners != null) {
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
}
