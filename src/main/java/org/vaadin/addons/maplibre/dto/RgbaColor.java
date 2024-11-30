package org.vaadin.addons.maplibre.dto;

public class RgbaColor extends RawColor implements Color {

    public RgbaColor(int r, int g, int b, double a) {
        super("rgba(" + r + ", " + g + ", " + b + ", " + a + ")");
    }

}
