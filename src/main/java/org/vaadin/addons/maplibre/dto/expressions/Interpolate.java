package org.vaadin.addons.maplibre.dto.expressions;

import com.fasterxml.jackson.annotation.JsonValue;
import org.vaadin.addons.maplibre.dto.AbstractDto;

import java.util.ArrayList;
import java.util.List;

public class Interpolate extends AbstractDto implements Expression {

    private static class InterpolationMode {
        @JsonValue
        private final List<Object> arguments = new ArrayList<>();

        public static InterpolationMode linear() {
            InterpolationMode mode = new InterpolationMode();
            mode.arguments.add("linear");
            return mode;
        }

        // TODO create examples/tests
        public static InterpolationMode exponential(Object base) {
            InterpolationMode mode = new InterpolationMode();
            mode.arguments.add("exponential");
            mode.arguments.add(base);
            return mode;
        }

        // TODO create examples/tests
        public static InterpolationMode cubicBezier(Object x1, Object y1, Object x2, Object y2) {
            InterpolationMode mode = new InterpolationMode();
            mode.arguments.add("cubic-bezier");
            mode.arguments.add(x1);
            mode.arguments.add(y1);
            mode.arguments.add(x2);
            mode.arguments.add(y2);
            return mode;
        }

    }

    @JsonValue
    private final List<Object> arguments = new ArrayList<>();

    private Interpolate() {
        arguments.add("interpolate");
    }

    public static Interpolate exponential(double base) {
        Interpolate interpolate = new Interpolate();
        interpolate.arguments.add(InterpolationMode.exponential(base));
        return interpolate;
    }

    public static Interpolate linear() {
        Interpolate interpolate = new Interpolate();
        interpolate.arguments.add(InterpolationMode.linear());
        return interpolate;
    }

    public static Interpolate cubicBezier(double x1, double y1, double x2, double y2) {
        Interpolate interpolate = new Interpolate();
        interpolate.arguments.add(InterpolationMode.cubicBezier(x1, y1, x2, y2));
        return interpolate;
    }

    public Interpolate zoom(ZoomStep... zoom) {
        arguments.add(arr("zoom"));
        for (ZoomStep step : zoom) {
            arguments.add(step.getZoom());
            arguments.add(step.getValue());
        }
        return this;
    }


}
