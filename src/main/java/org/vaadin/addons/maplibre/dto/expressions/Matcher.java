package org.vaadin.addons.maplibre.dto.expressions;

import com.fasterxml.jackson.annotation.JsonValue;

public abstract class Matcher implements Expression {

    private final String rule;
    private final Object value1;
    private final Object value2;

    public Matcher(String rule, Object value1, Object value2) {
        this.rule = rule;
        this.value1 = value1;
        this.value2 = value2;
    }

    @JsonValue
    public Object[] expression() {
        return new Object[]{rule, value1, value2};
    }

}
