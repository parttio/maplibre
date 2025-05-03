package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Point;

public class FlyToOptions extends AnimationOptions {

    private Double bearing = null;
    private Double zoom = null;
    private LngLat center = null;
    private Double pitch = null;

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    public Double getBearing() {
        return bearing;
    }

    public Double getZoom() {
        return zoom;
    }

    public void setZoom(Double zoom) {
        this.zoom = zoom;
    }

    public void setCenter(LngLat lngLat) {
        center = lngLat;
    }

    public LngLat getCenter() {
        return center;
    }

    public Double getPitch() {
        return pitch;
    }

    public void setPitch(Double pitch) {
        this.pitch = pitch;
    }

    public void setCenter(Point p) {
        center = LngLat.of(p);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return toString().length() == 2;
    }
}
