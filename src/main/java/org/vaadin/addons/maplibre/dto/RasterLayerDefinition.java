package org.vaadin.addons.maplibre.dto;

/**
 * A raster layer that renders a raster source, such as a {@link RasterMapSource}
 * (XYZ/TMS/WMTS tiles) or an {@link ImageMapSource} (a single static image).
 */
public class RasterLayerDefinition extends LayerDefinition {

    private RasterPaint paint;

    public RasterLayerDefinition() {
        super(null, LayerType.raster);
    }

    public RasterLayerDefinition(String id) {
        super(id, LayerType.raster);
    }

    /**
     * @param id     the id of the layer
     * @param source the id of the source to render
     */
    public RasterLayerDefinition(String id, String source) {
        super(id, LayerType.raster);
        setSource(source);
    }

    public RasterPaint getPaint() {
        return paint;
    }

    public void setPaint(RasterPaint paint) {
        this.paint = paint;
    }
}
