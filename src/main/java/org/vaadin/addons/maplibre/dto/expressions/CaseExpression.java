package org.vaadin.addons.maplibre.dto.expressions;

import com.fasterxml.jackson.annotation.JsonValue;
import org.vaadin.addons.maplibre.dto.AbstractDto;

import java.util.ArrayList;
import java.util.List;

public class CaseExpression extends AbstractDto implements Expression {

    public CaseExpression() {
    }

    public CaseExpression(Object defaultValue, Case... caseExpression) {
        this.caseExpression = List.of(caseExpression);
        this.defaultValue = defaultValue;
    }
    public CaseExpression(Object defaultValue, Case[]... caseExpression) {
        this.defaultValue = defaultValue;
        this.caseExpression = new ArrayList<>();
        for (Case[] c : caseExpression) {
            for (Case cc : c) {
                this.caseExpression.add(cc);
            }
        }
    }

    public void add(Case... cases) {
        for (Case c : cases) {
            caseExpression.add(c);
        }
    }

    private List<Case> caseExpression = new ArrayList<>();

    @JsonValue
    private List<Object> expression() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("case");
        caseExpression.forEach(c -> {
            objects.add(c.getRule());
            objects.add(c.getResult());
        });
        objects.add(defaultValue);
        return objects;
    }

    private Object defaultValue;

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
