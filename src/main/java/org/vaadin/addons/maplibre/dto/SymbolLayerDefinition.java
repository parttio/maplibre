package org.vaadin.addons.maplibre.dto;

public class SymbolLayerDefinition extends LayerDefinition {
    private SymbolLayout layout;
    public SymbolPaint paint;

    public SymbolLayerDefinition() {
        super(null, LayerType.symbol);
    }
    public SymbolLayerDefinition(String id) {
        super(id, LayerType.symbol);
    }

    public SymbolLayout getLayout() {
        return layout;
    }

    public void setLayout(SymbolLayout layout) {
        this.layout = layout;
    }

    public SymbolPaint getPaint() {
        return paint;
    }

    public void setPaint(SymbolPaint paint) {
        this.paint = paint;
    }
}
