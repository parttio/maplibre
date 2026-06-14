package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.dto.ImageMapSource;
import org.vaadin.addons.maplibre.dto.LngLat;
import org.vaadin.addons.maplibre.dto.RasterLayerDefinition;
import org.vaadin.addons.maplibre.dto.RasterPaint;
import org.vaadin.firitin.components.RichText;

import java.net.URI;

/**
 * Demonstrates {@link ImageMapSource}: a single static raster image georeferenced
 * on top of a vector base map by giving its four corner coordinates. Here we use a
 * scanned historical map (C. W. Gyldén's 1837 town plan of Turku/Åbo, from
 * Wikimedia Commons) overlaid on the modern map. The same approach works for an
 * aerial photo or any other georeferenced picture.
 * <p>
 * Note: the corner coordinates below are eyeballed for demonstration purposes only
 * (the source image also contains a title, an inset map and a legend table, so it
 * does not align perfectly). Adjust them to taste for a real georeferencing.
 */
@Route
public class OldMapImageOverlay extends VerticalLayout {

    static final String MAPTILER = "https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU";

    static final String OLD_MAP = "https://upload.wikimedia.org/wikipedia/commons/9/96/"
            + "Plan_af_%C3%85bo_stad_utgifven_1837_af_C._W._Gyld%C3%A9n.jpg";

    // Approximate WGS84 rectangle over central Turku for the overlay.
    static final double WEST = 22.235, EAST = 22.300, NORTH = 60.462, SOUTH = 60.428;

    public OldMapImageOverlay() throws Exception {
        add(new RichText().withMarkDown("""
                # Single image overlay (ImageMapSource)

                A scanned historical map — C. W. Gyldén's 1837 town plan of Turku (Åbo) from
                [Wikimedia Commons](https://commons.wikimedia.org/wiki/File:Plan_af_%C3%85bo_stad_utgifven_1837_af_C._W._Gyld%C3%A9n.jpg)
                — is georeferenced on top of a modern vector base map by giving its four corner
                coordinates. Use the field below to adjust the opacity (the `raster-opacity` paint
                property) to compare the old map with today's streets. The same works for aerial photos
                or any other georeferenced image.
                """));

        MapLibre map = new MapLibre(new URI(MAPTILER));
        map.setCenter((WEST + EAST) / 2, (NORTH + SOUTH) / 2);
        map.setZoomLevel(13);
        map.setWidth("100%");

        // Corners in MapLibre order: top-left, top-right, bottom-right, bottom-left
        ImageMapSource oldMap = new ImageMapSource(OLD_MAP,
                new LngLat(WEST, NORTH),
                new LngLat(EAST, NORTH),
                new LngLat(EAST, SOUTH),
                new LngLat(WEST, SOUTH));
        map.addSource("old-map", oldMap);

        RasterLayerDefinition layer = new RasterLayerDefinition("old-map", "old-map");
        layer.setPaint(new RasterPaint(0.7));
        map.addSourceLayer(layer);

        addAndExpand(map);
        add(new OpacityField(map, "old-map", 0.7));
    }
}
