package org.vaadin.addons.maplibre.dto;

import java.util.List;

/**
 * A MapLibre GL "image" source that places a single static raster image
 * (e.g. an aerial photo, a scanned old map or any other georeferenced overlay)
 * on top of the map. The image is positioned by its four corner coordinates.
 * <p>
 * Use together with a {@link RasterLayerDefinition} to actually render the image,
 * typically adjusting the {@link RasterPaint#setRasterOpacity(Double) opacity} so
 * the underlying base map remains partially visible.
 */
public class ImageMapSource extends AbstractUrlMapSource {

    /**
     * Corner coordinates of the image in [longitude, latitude] order, listed as
     * top-left, top-right, bottom-right and bottom-left.
     */
    private List<LngLat> coordinates;

    /**
     * @param url URL to the image to display. Must be CORS accessible.
     */
    public ImageMapSource(String url) {
        super("image", url);
    }

    /**
     * @param url         URL to the image to display. Must be CORS accessible.
     * @param coordinates the four corner coordinates of the image, in
     *                    [longitude, latitude] order, listed as top-left,
     *                    top-right, bottom-right and bottom-left.
     */
    public ImageMapSource(String url, List<LngLat> coordinates) {
        super("image", url);
        this.coordinates = coordinates;
    }

    /**
     * @param url         URL to the image to display. Must be CORS accessible.
     * @param topLeft     the top-left corner of the image
     * @param topRight    the top-right corner of the image
     * @param bottomRight the bottom-right corner of the image
     * @param bottomLeft  the bottom-left corner of the image
     */
    public ImageMapSource(String url, LngLat topLeft, LngLat topRight, LngLat bottomRight, LngLat bottomLeft) {
        super("image", url);
        setCoordinates(topLeft, topRight, bottomRight, bottomLeft);
    }

    public List<LngLat> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<LngLat> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Sets the image corners in the order MapLibre expects.
     *
     * @param topLeft     the top-left corner of the image
     * @param topRight    the top-right corner of the image
     * @param bottomRight the bottom-right corner of the image
     * @param bottomLeft  the bottom-left corner of the image
     */
    public void setCoordinates(LngLat topLeft, LngLat topRight, LngLat bottomRight, LngLat bottomLeft) {
        this.coordinates = List.of(topLeft, topRight, bottomRight, bottomLeft);
    }
}
