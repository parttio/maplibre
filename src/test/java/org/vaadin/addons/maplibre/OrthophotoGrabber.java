package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import org.locationtech.jts.geom.Coordinate;
import org.vaadin.addons.maplibre.dto.ImageMapSource;
import org.vaadin.addons.maplibre.dto.RasterLayerDefinition;
import org.vaadin.addons.maplibre.dto.RasterPaint;
import org.vaadin.addons.maplibre.dto.LngLat;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;
import org.vaadin.addons.maplibre.dto.expressions.ZoomStep;
import org.vaadin.firitin.components.RichText;

import java.net.URI;
import java.util.Locale;
import java.util.Map;

/**
 * Grabs a National Land Survey of Finland orthophoto around a chosen point: the
 * tiles are downloaded and stitched server side into a single JPG
 * ({@link TileStitcher}), served from the same origin
 * ({@link GeneratedOrthoController}) and then georeferenced on top of a MapTiler
 * vector base map with an {@link ImageMapSource}.
 * <p>
 * Pick a point by clicking the map, or use the test coordinate button.
 */
@Route
public class OrthophotoGrabber extends VerticalLayout {

    static final String MAPTILER = "https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU";

    // Kapsi orthophoto on the Finnish national EPSG:3067 (ETRS-TM35FIN) tile grid —
    // the live endpoint used by the kartat.kapsi.fi Leaflet example. (The Web
    // Mercator "ortokuva" endpoint redirects to a host that refuses connections, so
    // it cannot be used.) The grid is handled by TileStitcher.Tm35finGrid.
    static final String ORTHO_TEMPLATE = "https://tiles.kartat.kapsi.fi/ortokuva_3067/{z}/{x}/{y}.jpg";

    static final int ZOOM = 14;          // EPSG:3067 grid: 8192/2^z m/px -> z14 ≈ 0.5 m/px (z15 is the sharpest)
    static final double RADIUS_M = 200;  // ~400 m square around the point

    // The overlay fades out as you zoom out: fully transparent at/below this zoom,
    // fully opaque at/above the other one (linear in between).
    static final int FADE_OUT_ZOOM = 12;
    static final int FADE_IN_ZOOM = 15;

    // Test coordinate (lat, lon)
    static final double TEST_LAT = 60.0880962, TEST_LON = 22.0826187;

    private final MapLibre map;
    private final Select<Integer> zoomSelect = createZoomSelect();
    private final Pre details = new Pre("No orthophoto generated yet.");
    private SourceLayer current;
    private boolean started;
    private boolean attributionAdded;

    public OrthophotoGrabber() throws Exception {
        add(new RichText().withMarkDown("""
                # Orthophoto grabber

                Click anywhere on the map to download the orthophoto tiles around that point (~400&nbsp;m
                square), stitch them into a single JPG on the server, and overlay the result
                georeferenced on the map. Pick the resolution with the zoom selector (higher zoom = more
                detail but more tiles to download). The overlay fades out as you zoom out and is fully
                transparent at zoom %d and below (via a zoom based `raster-opacity` expression). The file
                and its location are saved on the server (temp dir).""".formatted(FADE_OUT_ZOOM)));

        map = new MapLibre(new URI(MAPTILER));
        map.setCenter(TEST_LON, TEST_LAT);
        map.setZoomLevel(15);
        map.setWidth("100%");

        map.addMapClickListener(e -> {
            Coordinate c = e.getCoordinate();
            generateAt(c.x, c.y);
        });

        addAndExpand(map);

        Button testButton = new Button("Grab at test coordinate (%.5f, %.5f)".formatted(TEST_LAT, TEST_LON),
                e -> generateAt(TEST_LON, TEST_LAT));
        HorizontalLayout controls = new HorizontalLayout(zoomSelect, testButton);
        controls.setAlignItems(Alignment.END);
        add(controls);

        details.getStyle().set("white-space", "pre-wrap").set("font-size", "0.8em");
        add(details);
    }

    private static Select<Integer> createZoomSelect() {
        Select<Integer> select = new Select<>();
        select.setLabel("Resolution (zoom)");
        select.setItems(13, 14, 15);
        // EPSG:3067 grid resolution is 8192/2^z meters/pixel.
        select.setItemLabelGenerator(z -> "z%d (~%.2f m/px)".formatted(z, 8192.0 / (1 << z)));
        select.setValue(ZOOM);
        return select;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if (!started) {
            started = true;
            // Run the test coordinate automatically on first load.
            generateAt(TEST_LON, TEST_LAT);
        }
    }

    private void generateAt(double lon, double lat) {
        UI ui = getUI().orElse(null);
        if (ui == null) {
            return;
        }
        int zoom = zoomSelect.getValue() != null ? zoomSelect.getValue() : ZOOM;
        Notification.show("Downloading and stitching orthophoto around %.5f, %.5f (z%d)…".formatted(lat, lon, zoom));
        Thread worker = new Thread(() -> {
            try {
                TileStitcher stitcher = new TileStitcher(ORTHO_TEMPLATE, new TileStitcher.Tm35finGrid());
                TileStitcher.Result result = stitcher.stitch(lon, lat, zoom, RADIUS_M);
                ui.access(() -> showOverlay(result));
            } catch (Exception ex) {
                ui.access(() -> Notification.show("Failed to grab orthophoto: " + ex.getMessage()));
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    private void showOverlay(TileStitcher.Result result) {
        if (current != null) {
            current.remove();
        }
        String url = GeneratedOrthoController.PATH + result.file().getName();
        ImageMapSource source = new ImageMapSource(url,
                result.topLeft(), result.topRight(), result.bottomRight(), result.bottomLeft());
        map.addSource("ortho-photo", source);

        RasterLayerDefinition layer = new RasterLayerDefinition("ortho-photo", "ortho-photo");
        // Zoom based fade: transparent at/below FADE_OUT_ZOOM, opaque at/above FADE_IN_ZOOM.
        RasterPaint paint = new RasterPaint();
        paint.setRasterOpacity(Interpolate.linear().zoom(
                new ZoomStep(FADE_OUT_ZOOM, 0.0),
                new ZoomStep(FADE_IN_ZOOM, 1.0)));
        layer.setPaint(paint);
        current = map.addSourceLayer(layer);
        addMmlAttribution();

        map.flyTo(result.centerLon(), result.centerLat(), 16.0);

        details.setText("""
                File on server: %s
                Zoom: %d   Tiles: %d/%d
                Corner coordinates (lng, lat):
                  top-left:     %s
                  top-right:    %s
                  bottom-right: %s
                  bottom-left:  %s"""
                .formatted(result.file().getAbsolutePath(), result.zoom(),
                        result.tilesOk(), result.tilesTotal(),
                        formatLngLat(result.topLeft()), formatLngLat(result.topRight()),
                        formatLngLat(result.bottomRight()), formatLngLat(result.bottomLeft())));
        Notification.show("Orthophoto placed (%d/%d tiles).".formatted(result.tilesOk(), result.tilesTotal()));
    }

    private static String formatLngLat(LngLat ll) {
        return String.format(Locale.ROOT, "%.6f, %.6f", ll.lng(), ll.lat());
    }

    /**
     * MapLibre's "image" source type does not support an attribution property, so
     * the credit cannot be attached to the overlay itself. Instead we replace the
     * default attribution control with one that adds a custom attribution — it still
     * aggregates the base map's source attributions, so MML ends up in the same
     * copyright listing rather than a separate box. Done once.
     */
    private void addMmlAttribution() {
        if (attributionAdded) {
            return;
        }
        attributionAdded = true;
        map.js("""
                const existing = (map._controls || []).find(c => c instanceof maplibregl.AttributionControl);
                if (existing) { map.removeControl(existing); }
                map.addControl(new maplibregl.AttributionControl({ customAttribution: '© Maanmittauslaitos' }));
                """, Map.of());
    }
}
