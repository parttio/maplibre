package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.textfield.NumberField;

import java.util.Map;

/**
 * A small reusable field that adjusts the {@code raster-opacity} paint property
 * of a raster layer live via the MapLibre GL JS API.
 */
public class OpacityField extends NumberField {

    public OpacityField(MapLibre map, String layerId, double initialValue) {
        setLabel("Opacity (raster-opacity)");
        setMin(0);
        setMax(1);
        setStep(0.05);
        setValue(initialValue);
        setWidth("16em");
        addValueChangeListener(e -> {
            double value = e.getValue() == null ? 0 : e.getValue();
            map.js("map.setPaintProperty('$layer', 'raster-opacity', $value);",
                    Map.of("layer", layerId, "value", String.valueOf(value)));
        });
    }
}
