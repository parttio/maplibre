package org.vaadin.addons.maplibre.dto.expressions;

import org.vaadin.addons.maplibre.dto.AbstractDto;

public abstract class Case extends AbstractDto {

    private final Object rule;
    private final Object result;

    public Case(Object rule, Object result) {
        this.rule = rule;
        this.result = result;
    }

    public Object getRule() {
        return rule;
    }

    public Object getResult() {
        return result;
    }
}
