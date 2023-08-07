package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.Map;

public class Marker extends Layer {


    Marker(MapLibre map, String id, Coordinate coordinate) {
        super(map, id,new GeometryFactory().createPoint(coordinate));
    }

    public void withPopup(String html) {
        map.js("""
            const marker = component.markers['$id'];
            const popup = new maplibregl.Popup({closeButton: true, closeOnClick: true})
                .setLngLat(marker.getLngLat())
                .setHTML('$html');
            marker.setPopup(popup);
        """.formatted(id, html), Map.of("id", id, "html", html));
    }
}
