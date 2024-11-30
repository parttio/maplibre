package org.vaadin.addons.maplibre.dto.expressions;

import com.fasterxml.jackson.annotation.JsonValue;
import org.vaadin.addons.maplibre.dto.AbstractDto;

import java.util.ArrayList;
import java.util.List;

public class Any extends AbstractDto implements Expression {

    public Any() {
    }

    public Any(Matcher... predicates) {
        this.predicates = List.of(predicates);
    }

    private List<Matcher> predicates = new ArrayList<>();

    @JsonValue
    private List<Object> expression() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("any");
        objects.addAll(predicates);
        return objects;
    }
}
