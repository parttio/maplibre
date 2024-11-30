package org.vaadin.addons.maplibre.dto;

public class CircleLayerDefinition extends LayerDefinition {

    private CirclePaint paint;

    public CircleLayerDefinition() {
        super(null, LayerType.circle);
    }
    public CircleLayerDefinition(String id) {
        super(id, LayerType.circle);
    }

    public CirclePaint getPaint() {
        return paint;
    }

    public void setPaint(CirclePaint paint) {
        this.paint = paint;
    }

}
