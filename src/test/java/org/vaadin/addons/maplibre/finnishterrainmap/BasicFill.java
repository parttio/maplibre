package org.vaadin.addons.maplibre.finnishterrainmap;

import in.virit.color.Color;
import in.virit.color.HexColor;
import org.vaadin.addons.maplibre.FillPaint;
import org.vaadin.addons.maplibre.dto.FillLayerDefinition;

public class BasicFill extends FillLayerDefinition {

    public BasicFill(String layer, String hexColor) {
        this(layer, HexColor.of(hexColor));
    }

    public BasicFill(String layer, Color color) {
        super(layer);
        setSource("mtk");
        setSourceLayer(layer);
        setPaint(new FillPaint(color));
    }
}
