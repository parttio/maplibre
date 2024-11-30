package org.vaadin.addons.maplibre.dto;

import org.vaadin.addons.maplibre.FillPaint;

public class FillLayerDefinition extends LayerDefinition {
    private FillPaint paint;

    public FillLayerDefinition() {
        super(null, LayerType.fill);
    }
    public FillLayerDefinition(String id) {
        super(id, LayerType.fill);
    }

    public FillPaint getPaint() {
        return paint;
    }

    public void setPaint(FillPaint paint) {
        this.paint = paint;
    }
}
