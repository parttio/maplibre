package org.vaadin.addons.maplibre.dto;

public interface Color {

    public static final Color BLUE = new RawColor("blue");

    // TODO validations

    public static Color hex(String webhex) {
        return new RawColor(webhex);
    };

    public static Color rgba(int r, int g, int b, double a) {
        return new RgbaColor(r, g, b, a);
    }

    public static Color named(String name) {
        return new RawColor(name);
    }
}
