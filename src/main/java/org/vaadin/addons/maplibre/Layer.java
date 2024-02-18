package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.Geometry;

public class Layer {
    final String id;
    final MapLibre map;

    final Geometry geometry;

    Layer(MapLibre map, String id, Geometry geometry) {
        this.map = map;
        this.id = id;
        this.geometry = geometry;
        map.registerLayer(id, this);
    }

    public void remove() {
        map.removeLayer(this);
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
