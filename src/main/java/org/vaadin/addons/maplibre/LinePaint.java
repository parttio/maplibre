package org.vaadin.addons.maplibre;

import org.vaadin.addons.maplibre.dto.AbstractKebabCasedDto;
import org.vaadin.addons.maplibre.dto.Color;

public class LinePaint extends AbstractKebabCasedDto {

    Color lineColor;
    Double lineWidth;

    public LinePaint(Color lineColor) {
        this.lineColor = lineColor;
    }

    public LinePaint(Color lineColor, Double lineWidth) {
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    public Double getLineWidth() {
        return lineWidth;
    }

    public Color getLineColor() {
        return lineColor;
    }
}
