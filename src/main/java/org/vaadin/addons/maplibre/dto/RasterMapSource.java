package org.vaadin.addons.maplibre.dto;

public class RasterMapSource extends AbstractTileMapSource {

    private Integer tileSize;

    public RasterMapSource(String url) {
        super("raster", url);
    }

    public Integer getTileSize() {
        return tileSize;
    }

    public void setTileSize(Integer tileSize) {
        this.tileSize = tileSize;
    }
}
