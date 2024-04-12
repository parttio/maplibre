package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

@JsonSerialize(using = LngLatSerializer.class)
public record LngLat(double lng, double lat) {

    public static LngLat of(Point p) {
        return new LngLat(p.getX(), p.getY());
    }

    public static LngLat of(Coordinate c) {
        return new LngLat(c.getX(),c.getY());
    }
}
