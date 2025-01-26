package org.vaadin.addons.maplibre.dto.expressions;

import org.vaadin.addons.maplibre.dto.AbstractDto;

import java.util.List;

public class PropertyEquals extends Case {
    public PropertyEquals(String propertyName, Object machedValue, Object result) {
        super(AbstractDto.arr("==", AbstractDto.arr("get", propertyName), machedValue), result);
    }

    public static PropertyEquals[] anyValue(String propertyName, Object result, Object... matchedValues) {
        PropertyEquals[] propertyEquals = new PropertyEquals[matchedValues.length];
        for (int i = 0; i < matchedValues.length; i++) {
            propertyEquals[i] = new PropertyEquals(propertyName, matchedValues[i], result);
        }
        return propertyEquals;
    }

    public static PropertyEquals[] anyValue(String propertyName, Object result, List<?> matchedValues) {
        return matchedValues.stream().map(value -> new PropertyEquals(propertyName, value, result)).toArray(PropertyEquals[]::new);
    }

}
