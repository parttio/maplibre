package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.geolocation.Geolocation;
import org.vaadin.firitin.geolocation.GeolocationCoordinates;
import org.vaadin.firitin.geolocation.GeolocationOptions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Route
public class GpsTrackingAndRotation extends VerticalLayout {
    static GeometryFactory gf = new GeometryFactory();

    private Marker marker;
    private Geolocation geolocation;
    private List<Coordinate> tailpoints = new ArrayList<>();
    private LineLayer tail;

    public GpsTrackingAndRotation() {
        MapLibre map = new MapLibre("https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU");
        map.setHeight("400px");
        map.setWidth("100%");
        map.setCenter(24.945831, 60.192059);
        map.setZoomLevel(15);
        add(map);

        var options = new GeolocationOptions();
        options.setEnableHighAccuracy(true);
        geolocation = Geolocation.watchPosition(geolocation -> {
            var coords = geolocation.getCoords();
            Coordinate coordinate = new Coordinate(coords.getLongitude(), coords.getLatitude(), 0);

            if (marker == null) {
                marker = map.addMarker(coords.getLongitude(), coords.getLatitude());
                marker.setColor("#009900");
                map.flyTo(marker.getGeometry());
            } else {
                // update position
                var p = gf.createPoint(coordinate);
                marker.setPoint(p);

                Double heading = geolocation.getCoords().getHeading();
                // heading is only available if moving, map will tilt then
                if(heading != null) {
                    map.setBearing(heading);
                }
                map.getViewPort().thenAccept(viewPort -> {
                    // adjust if marker has moved out of viewport
                    if(!viewPort.getBounds().contains(p)) {
                        map.flyTo(p);
                    }
                });
            }
            tailpoints.add(coordinate);
            if (tailpoints.size() > 100) {
                tailpoints.remove(0);
            }
            if (tailpoints.size() == 2) {
                var ls = gf.createLineString(tailpoints.toArray(new Coordinate[0]));
                // create and start showing the tail
                tail = map.addLineLayer(ls, new LinePaint("#00ff00", 2.0));
            } else if (tailpoints.size() > 2) {
                // update tail
                tail.addCoordinates(tailpoints.size() == 100 ? 1 : 0, tailpoints.get(tailpoints.size() - 1));
            }

        }, error -> {
            Notification.show(error.getErrorMessage());
        }, options);

    }
}
