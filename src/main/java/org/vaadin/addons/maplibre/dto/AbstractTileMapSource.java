package org.vaadin.addons.maplibre.dto;

public class AbstractTileMapSource extends AbstractUrlMapSource {

    /**
     *
     * @param type The type of the source. Must be 'raster', 'raster-dem', 'vector', 'raster-tile' or 'geojson'.
     * @param url A URL to a TileJSON resource. Supported protocols are http: and https:.
     */
    AbstractTileMapSource(String type, String url) {
        super(type, url);
    }

    private String attribution;

    /**
     * Optional array.
     *
     * An array of one or more tile source URLs, as in the TileJSON spec.
     */
    private String[] tiles;

    private Integer minzoom;
    private Integer maxzoom;
    private Bounds bounds;
    private String scheme;


    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public String[] getTiles() {
        return tiles;
    }

    public void setTiles(String[] tiles) {
        this.tiles = tiles;
    }

    public Integer getMinzoom() {
        return minzoom;
    }

    public void setMinzoom(Integer minzoom) {
        this.minzoom = minzoom;
    }

    public Integer getMaxzoom() {
        return maxzoom;
    }

    public void setMaxzoom(Integer maxzoom) {
        this.maxzoom = maxzoom;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
}
