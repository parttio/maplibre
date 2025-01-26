package org.vaadin.addons.maplibre.dto.expressions;

import com.fasterxml.jackson.annotation.JsonValue;
import org.vaadin.addons.maplibre.dto.AbstractDto;

import java.util.ArrayList;
import java.util.List;

public class All extends AbstractDto implements Expression {

    public All() {
    }

    public All(Expression... predicates) {
        this.predicates = List.of(predicates);
    }

    private List<Expression> predicates = new ArrayList<>();

    @JsonValue
    private List<Object> expression() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("all");
        objects.addAll(predicates);
        return objects;
    }
}
