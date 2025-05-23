package org.vaadin.addons.maplibre;

import in.virit.color.Color;
import org.vaadin.addons.maplibre.dto.AbstractKebabCasedDto;
import org.vaadin.addons.maplibre.dto.expressions.Expression;

public class LinePaint extends AbstractKebabCasedDto {

    Object lineColor;
    Object lineWidth;
    private int[] lineDasharray;

    public void setLineDasharray(int... dasharray) {
        this.lineDasharray = dasharray;
    }

    public int[] getLineDasharray() {
        return lineDasharray;
    }

    public LinePaint() {
    }

    public LinePaint(Color lineColor) {
        this.lineColor = lineColor;
    }

    public LinePaint(Color lineColor, Double lineWidth) {
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    public Object getLineWidth() {
        return lineWidth;
    }

    public Object getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color color) {
        this.lineColor = color;
    }

    public void setLineColor(Expression color) {
        this.lineColor = color;
    }

    public void setLineWidth(Expression lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setLineWidth(Double lineWidth) {
        this.lineWidth = lineWidth;
    }

}
