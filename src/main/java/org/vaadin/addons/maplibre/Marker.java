package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

public class Marker extends Layer {


    Marker(MapLibre map, String id, Coordinate coordinate) {
        super(map, id,new GeometryFactory().createPoint(coordinate));
    }

    public void withPopup(String html) {
        map.js("""
            const marker = component.markers['%s'];
            const popup = new maplibregl.Popup({closeButton: true, closeOnClick: true})
                .setLngLat(marker.getLngLat())
                .setHTML('%s');
            marker.setPopup(popup);
        """.formatted(id, html));
    }
}
