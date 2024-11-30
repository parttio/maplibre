package org.vaadin.addons.maplibre.dto.expressions;

import java.util.List;

public class Equals extends Matcher {

    public Equals(String propertyName, Object propertyValue) {
        super("==", propertyName, propertyValue);
    }

    public static Equals[] anyValue(String propertyName, Object... values) {
        Equals[] matchers = new Equals[values.length];
        for (int i = 0; i < values.length; i++) {
            matchers[i] = new Equals(propertyName, values[i]);
        }
        return matchers;
    }

    public static Equals[] anyValue(String propertyName, List<?> values) {
        return values.stream().map(value -> new Equals(propertyName, value)).toArray(Equals[]::new);
    }

}
