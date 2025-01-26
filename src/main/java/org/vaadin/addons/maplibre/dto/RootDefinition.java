package org.vaadin.addons.maplibre.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootDefinition extends AbstractKebabCasedDto {

    private final Integer version = 8; // 8 is the latest version of MapLibre GL JS API

    private String name;

    private String sprite;

    private String glyphs;

    private Projection projection;

    private Map<String,AbstractMapSource> sources = new HashMap<>();

    private List<LayerDefinition> layers = new ArrayList<>();

    public Integer getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public String getGlyphs() {
        return glyphs;
    }

    public void setGlyphs(String glyphs) {
        this.glyphs = glyphs;
    }

    public List<LayerDefinition> getLayers() {
        return layers;
    }

    public void setLayers(List<LayerDefinition> layers) {
        this.layers = layers;
    }

    public Map<String, AbstractMapSource> getSources() {
        return sources;
    }

    public void setSources(Map<String, AbstractMapSource> sources) {
        this.sources = sources;
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }
}
