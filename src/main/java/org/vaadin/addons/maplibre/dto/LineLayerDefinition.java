package org.vaadin.addons.maplibre.dto;

import org.vaadin.addons.maplibre.LinePaint;

public class LineLayerDefinition extends LayerDefinition {
    private LinePaint paint;

    public LineLayerDefinition() {
        super(null, LayerType.line);
    }
    public LineLayerDefinition(String id) {
        super(id, LayerType.line);
    }

    public LinePaint getPaint() {
        return paint;
    }

    public void setPaint(LinePaint paint) {
        this.paint = paint;
    }
}
