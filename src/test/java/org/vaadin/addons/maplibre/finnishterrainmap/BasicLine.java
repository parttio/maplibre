package org.vaadin.addons.maplibre.finnishterrainmap;

import in.virit.color.Color;
import in.virit.color.HexColor;
import org.vaadin.addons.maplibre.LinePaint;
import org.vaadin.addons.maplibre.dto.LineLayerDefinition;

public class BasicLine extends LineLayerDefinition {

    public BasicLine(String layer, String hexColor) {
        this(layer, new HexColor(hexColor));
    }

    public BasicLine(String layer, Color color) {
        super(layer);
        setSource("mtk");
        setSourceLayer(layer);
        setPaint(new LinePaint(color));
    }
}
