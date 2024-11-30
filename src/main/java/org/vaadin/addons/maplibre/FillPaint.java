package org.vaadin.addons.maplibre;

import org.vaadin.addons.maplibre.dto.AbstractKebabCasedDto;

public class FillPaint extends AbstractKebabCasedDto {

    String fillColor;
    Double fillOpacity;

    public FillPaint(String fillColor) {
        this.fillColor = fillColor;
    }
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
