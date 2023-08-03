package org.vaadin.addons.maplibre;

public class FillPaint extends AbstractKebabCasedDto {

    String fillColor;
    Double fillOpacity;

    public FillPaint(String fillColor, Double fillOpacity) {
        this.fillColor = fillColor;
        this.fillOpacity = fillOpacity;
    }

    public Double getFillOpacity() {
        return fillOpacity;
    }

    public String getFillColor() {
        return fillColor;
    }
}
