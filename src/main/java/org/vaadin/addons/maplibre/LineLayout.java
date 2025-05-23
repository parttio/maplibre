package org.vaadin.addons.maplibre;

import org.vaadin.addons.maplibre.dto.AbstractKebabCasedDto;
import org.vaadin.addons.maplibre.dto.expressions.Expression;

public class LineLayout extends AbstractKebabCasedDto {

    public enum LineCap {
        butt, round, square
    }

    LineCap lineCap;

    public LineCap getLineCap() {
        return lineCap;
    }

    public void setLineCap(LineCap lineCap) {
        this.lineCap = lineCap;
    }
}
