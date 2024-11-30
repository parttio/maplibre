package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public class Bounds {

    @JsonValue
    private double[] bounds;

    public Bounds(double swLng, double swLat, double neLng, double neLat) {
        this.bounds = new double[]{swLng, swLat, neLng, neLat};
    }

    public Bounds(LngLat sw, LngLat ne) {
        this.bounds = new double[]{sw.lat(), sw.lng(), ne.lat(), ne.lng()};
    }

    public LngLat getSouthWest() {
        return new LngLat(bounds[0], bounds[1]);
    }

    public LngLat getNorthEast() {
        return new LngLat(bounds[2], bounds[3]);
    }

    public double[] getBounds() {
        return bounds;
    }

    public void setBounds(double[] bounds) {
        this.bounds = bounds;
    }
}
