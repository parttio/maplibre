package org.vaadin.addons.maplibre.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RootDefinition extends AbstractKebabCasedDto {

    private final Integer version = 8; // 8 is the latest version of MapLibre GL JS API

    private String name;

    record SpriteDef(String id, String url) {}

    private List<SpriteDef> sprite;

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

    public List<SpriteDef> getSprite() {
        return sprite;
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

    public void addSource(String id, AbstractMapSource source) {
        // TODO consider putting id to MapSource, although it is not that way in MapLibre itself
        getSources().put(id, source);
    }

    public void addSprite(String id, String url) {
        if(sprite == null) {
            sprite = new ArrayList<>();
        }
        sprite.add(new SpriteDef(id, url));
    }

    public void addSourceLayer(LayerDefinition layerDefinition) {
        String id = layerDefinition.getId();
        if(id == null) {
            layerDefinition.setId(UUID.randomUUID().toString());
        }
        layers.add(layerDefinition);
    }
}
