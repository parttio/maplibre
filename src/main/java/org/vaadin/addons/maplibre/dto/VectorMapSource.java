package org.vaadin.addons.maplibre.dto;

public class VectorMapSource extends AbstractTileMapSource {

    private String scheme;

    public VectorMapSource(String url) {
        super("vector", url);
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
}
