package org.vaadin.addons.maplibre.dto;

public class RasterMapSource extends AbstractTileMapSource {

    private Integer tileSize;

    /**
     * Creates a raster source without a TileJSON url. Typically used together with
     * {@link #setTiles(String[])} to point to an XYZ/TMS tile service, see also
     * {@link #ofTiles(String...)}.
     */
    public RasterMapSource() {
        super("raster", null);
    }

    /**
     * @param url A URL to a TileJSON resource describing the raster tiles.
     */
    public RasterMapSource(String url) {
        super("raster", url);
    }

    /**
     * Creates a raster source from one or more XYZ/TMS tile URL templates, e.g.
     * {@code "https://example.com/tiles/{z}/{x}/{y}.jpg"}. For a TMS (flipped Y)
     * service, additionally call {@link #setScheme(String) setScheme("tms")}.
     *
     * @param tiles one or more tile URL templates
     * @return a new raster source
     */
    public static RasterMapSource ofTiles(String... tiles) {
        RasterMapSource source = new RasterMapSource();
        source.setTiles(tiles);
        return source;
    }

    public Integer getTileSize() {
        return tileSize;
    }

    public void setTileSize(Integer tileSize) {
        this.tileSize = tileSize;
    }
}
