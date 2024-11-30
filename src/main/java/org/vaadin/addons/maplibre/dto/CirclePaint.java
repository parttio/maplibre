package org.vaadin.addons.maplibre.dto;

import org.vaadin.addons.maplibre.dto.expressions.CaseExpression;
import org.vaadin.addons.maplibre.dto.expressions.Expression;

public class CirclePaint extends AbstractKebabCasedDto {
    private Object circleColor;
    private String circleStrokeColor;
    private Object circleRadius;
    private Double circleStrokeWidth;
    private Double circleOpacity;

    public Object getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(String circleColor) {
        this.circleColor = circleColor;
    }

    public void setCircleColor(CaseExpression circleColor) {
        this.circleColor = circleColor;
    }

    public String getCircleStrokeColor() {
        return circleStrokeColor;
    }

    public void setCircleStrokeColor(String circleStrokeColor) {
        this.circleStrokeColor = circleStrokeColor;
    }

    public Object getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(Double circleRadius) {
        this.circleRadius = circleRadius;
    }

    public void setCircleRadius(Expression circleRadius) {
        this.circleRadius = circleRadius;
    }

    public Double getCircleStrokeWidth() {
        return circleStrokeWidth;
    }

    public void setCircleStrokeWidth(Double circleStrokeWidth) {
        this.circleStrokeWidth = circleStrokeWidth;
    }

    public void setCircleStrokeWidth(Integer circleStrokeWidth) {
        this.circleStrokeWidth = circleStrokeWidth == null ? null : circleStrokeWidth.doubleValue();
    }

    public Double getCircleOpacity() {
        return circleOpacity;
    }

    public void setCircleOpacity(Double circleOpacity) {
        this.circleOpacity = circleOpacity;
    }

    public void setCircleOpacity(Integer circleOpacity) {
        this.circleOpacity = circleOpacity == null ? null : circleOpacity.doubleValue();
    }

}
