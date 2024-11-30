package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.vaadin.addons.maplibre.dto.expressions.Expression;

public class LayerDefinition extends AbstractKebabCasedDto {

    private String id;
    private final LayerType type;
    private String source;
    private String sourceLayer;
    @JsonProperty("minzoom")
    private Integer minZoom;
    @JsonProperty("maxzoom")
    private Integer maxZoom;
    private Expression filter;

    public LayerDefinition(String id, LayerType type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LayerType getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceLayer() {
        return sourceLayer;
    }

    public void setSourceLayer(String sourceLayer) {
        this.sourceLayer = sourceLayer;
    }

    public Integer getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(Integer minZoom) {
        this.minZoom = minZoom;
    }

    public Integer getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(Integer maxZoom) {
        this.maxZoom = maxZoom;
    }

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    public enum LayerType {
        fill, line, symbol, circle, heatmap, fill_extrusion, raster, hillshade, background;

        @Override
        public String toString() {
            return name().replace("_", "-");
        }
    }

}
