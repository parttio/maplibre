package org.vaadin.addons.maplibre;

import in.virit.color.Color;
import org.vaadin.addons.maplibre.dto.AbstractKebabCasedDto;
import org.vaadin.addons.maplibre.dto.expressions.Expression;

public class FillPaint extends AbstractKebabCasedDto {

    Object fillColor;
    Double fillOpacity;

    public FillPaint(String fillColor) {
        this.fillColor = Color.parseCssColor(fillColor).toString();
    }
    public FillPaint(Color fillColor) {
        this.fillColor = fillColor;
    }
    public FillPaint(Color fillColor, Double fillOpacity) {
        this.fillColor = fillColor.toString();
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
