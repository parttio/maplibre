package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.Geometry;

public class GeometryLayer extends Layer {

    Geometry geometry;

    GeometryLayer(MapLibre map, String id, Geometry geometry) {
        super(id, map);
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }
}
