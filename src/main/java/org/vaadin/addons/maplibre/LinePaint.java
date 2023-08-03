package org.vaadin.addons.maplibre;

public class LinePaint extends AbstractKebabCasedDto {

    String lineColor;
    Double lineWidth;

    public LinePaint(String lineColor, Double lineWidth) {
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }

    public Double getLineWidth() {
        return lineWidth;
    }

    public String getLineColor() {
        return lineColor;
    }
}
