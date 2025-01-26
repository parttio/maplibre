package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public class RawColor extends AbstractKebabCasedDto implements Color {

    @JsonValue
    private String rawColor;

    public RawColor(String rawColor) {
        this.rawColor = rawColor;
    }

}
