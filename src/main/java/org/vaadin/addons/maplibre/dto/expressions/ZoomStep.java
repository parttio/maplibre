package org.vaadin.addons.maplibre.dto.expressions;

import in.virit.color.Color;

public class ZoomStep {
    private final int zoom;
    private final Object value;

    public ZoomStep(int zoom, String value) {
        this.zoom = zoom;
        this.value = value;
    }

    public ZoomStep(int zoom, double value) {
        this.zoom = zoom;
        this.value = value;
    }

    public ZoomStep(int zoom, int value) {
        this.zoom = zoom;
        this.value = value;
    }

    public ZoomStep(int zoom, Color value) {
        this.zoom = zoom;
        this.value = value;
    }

    public ZoomStep(int zoom, Expression expression) {
        this.zoom = zoom;
        this.value = expression;
    }

    public int getZoom() {
        return zoom;
    }

    public Object getValue() {
        return value;
    }
}
