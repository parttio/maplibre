package org.vaadin.addons.maplibre.dto.expressions;

import com.fasterxml.jackson.annotation.JsonValue;
import org.vaadin.addons.maplibre.dto.AbstractDto;

import java.util.ArrayList;
import java.util.List;

public class In extends AbstractDto implements Expression {

    private final String propertyName;

    public In(String propertyName, Object... matchedValues) {
        this.propertyName = propertyName;
        this.matchedValues = List.of(matchedValues);
    }

    public In(String propertyName, List matchedValues) {
        this.propertyName = propertyName;
        this.matchedValues = matchedValues;
    }

    private List<Object> matchedValues = new ArrayList<>();

    @JsonValue
    private List<Object> expression() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("in");
        objects.add(propertyName);
        objects.addAll(matchedValues);
        return objects;
    }
}