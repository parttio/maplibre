package org.vaadin.addons.maplibre.dto.expressions;

import java.util.List;

public class NotEquals extends Matcher {

    public NotEquals(String propertyName, Object propertyValue) {
        super("!=", propertyName, propertyValue);
    }

    public static Matcher[] anyValue(String propertyName, Object... values) {
        Matcher[] matchers = new Matcher[values.length];
        for (int i = 0; i < values.length; i++) {
            matchers[i] = new NotEquals(propertyName, values[i]);
        }
        return matchers;
    }

    public static Matcher[] anyValue(String propertyName, List<?> values) {
        return values.stream().map(value -> new NotEquals(propertyName, value)).toArray(Matcher[]::new);
    }
}
