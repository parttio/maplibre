package org.vaadin.addons.maplibre;

import org.vaadin.addons.maplibre.dto.AbstractKebabCasedDto;
import org.vaadin.addons.maplibre.dto.Color;
import org.vaadin.addons.maplibre.dto.RawColor;
import org.vaadin.addons.maplibre.dto.expressions.Expression;

public class FillPaint extends AbstractKebabCasedDto {

    Object fillColor;
    Double fillOpacity;

    public FillPaint(String fillColor) {
        this.fillColor = new RawColor(fillColor);
    }
    public FillPaint(Color fillColor) {
        this.fillColor = fillColor;
    }
    public FillPaint(String fillColor, Double fillOpacity) {
        this.fillColor = new RawColor(fillColor);
        this.fillOpacity = fillOpacity;
    }

    public Double getFillOpacity() {
        return fillOpacity;
    }

    public Object getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void setFillColor(Expression fillColor) {
        this.fillColor = fillColor;
    }
}
