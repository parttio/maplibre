package org.vaadin.addons.maplibre;

import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.ProjCoordinate;
import org.vaadin.addons.maplibre.dto.LngLat;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A small utility that downloads individual map tiles around a given coordinate at
 * a chosen zoom level, stitches them into a single JPG file and records the
 * geographic location (the four WGS84 corner coordinates) of the resulting image.
 * <p>
 * Supports both Web Mercator (EPSG:3857) tile services and the Finnish national
 * EPSG:3067 (ETRS-TM35FIN) tile grid used by e.g. kartat.kapsi.fi. The stitched
 * image's grid-aligned bounds are reprojected to WGS84 corners so it can be placed
 * with an {@link org.vaadin.addons.maplibre.dto.ImageMapSource}.
 */
public class TileStitcher {

    /** Where stitched images and their sidecar metadata are written. */
    public static final File OUTPUT_DIR = new File(System.getProperty("java.io.tmpdir"), "maplibre-orthophotos");

    public static final int TILE_SIZE = 256;

    private static final String UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/124.0 Safari/537.36";

    /**
     * The result of a stitch operation: the written JPG file, the WGS84 corner
     * coordinates (top-left, top-right, bottom-right, bottom-left) and some metadata.
     */
    public record Result(File file, LngLat topLeft, LngLat topRight, LngLat bottomRight, LngLat bottomLeft,
                         double centerLon, double centerLat, int zoom, int tilesOk, int tilesTotal) {
    }

    /** A tile grid: maps coordinates to tile col/row and back to WGS84 corners. */
    public interface TileGrid {
        int colFor(double lon, double lat, int z);

        int rowFor(double lon, double lat, int z);

        /** Side of a single tile in meters at the given zoom (and latitude, if relevant). */
        double tileSpanMeters(int z, double lat);

        /** WGS84 [lon, lat] of the north-west corner of the cell (col, row). */
        LngLat cornerLonLat(int col, int row, int z);
    }

    private final String urlTemplate;
    private final TileGrid grid;
    private final HttpClient http = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    /**
     * @param urlTemplate tile URL template with {@code {z}}, {@code {x}} (=column) and
     *                    {@code {y}} (=row) placeholders
     * @param grid        the tile grid the service uses
     */
    public TileStitcher(String urlTemplate, TileGrid grid) {
        this.urlTemplate = urlTemplate;
        this.grid = grid;
    }

    /**
     * Downloads and stitches the tiles covering a square of about
     * {@code 2 * radiusMeters} on a side around the given point.
     */
    public Result stitch(double lon, double lat, int zoom, double radiusMeters) throws Exception {
        double span = grid.tileSpanMeters(zoom, lat);
        int reach = Math.max(0, (int) Math.ceil(radiusMeters / span));

        int col0 = grid.colFor(lon, lat, zoom);
        int row0 = grid.rowFor(lon, lat, zoom);
        int colMin = col0 - reach, colMax = col0 + reach;
        int rowMin = row0 - reach, rowMax = row0 + reach;
        int cols = colMax - colMin + 1;
        int rows = rowMax - rowMin + 1;

        BufferedImage canvas = new BufferedImage(cols * TILE_SIZE, rows * TILE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        AtomicInteger ok = new AtomicInteger();

        List<int[]> tiles = new ArrayList<>();
        for (int c = colMin; c <= colMax; c++) {
            for (int r = rowMin; r <= rowMax; r++) {
                tiles.add(new int[]{c, r});
            }
        }

        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<TileImage>> futures = new ArrayList<>();
            for (int[] t : tiles) {
                final int c = t[0], r = t[1];
                futures.add(pool.submit(() -> new TileImage(c, r, download(zoom, c, r))));
            }
            for (Future<TileImage> f : futures) {
                TileImage ti = f.get();
                if (ti.image != null) {
                    g.drawImage(ti.image, (ti.col - colMin) * TILE_SIZE, (ti.row - rowMin) * TILE_SIZE, null);
                    ok.incrementAndGet();
                }
            }
        } finally {
            pool.shutdown();
            g.dispose();
        }

        LngLat topLeft = grid.cornerLonLat(colMin, rowMin, zoom);
        LngLat topRight = grid.cornerLonLat(colMax + 1, rowMin, zoom);
        LngLat bottomRight = grid.cornerLonLat(colMax + 1, rowMax + 1, zoom);
        LngLat bottomLeft = grid.cornerLonLat(colMin, rowMax + 1, zoom);

        OUTPUT_DIR.mkdirs();
        // Locale.ROOT so the decimal separator is always '.' (not e.g. ',' in fi-FI),
        // otherwise the file name would contain commas and break the serving URL.
        String base = String.format(Locale.ROOT, "ortho_%.6f_%.6f_z%d", lat, lon, zoom).replace('.', '_');
        File jpg = new File(OUTPUT_DIR, base + ".jpg");
        ImageIO.write(canvas, "jpg", jpg);

        Result result = new Result(jpg, topLeft, topRight, bottomRight, bottomLeft,
                lon, lat, zoom, ok.get(), tiles.size());
        // Save the location of the image next to it (corners as a sidecar).
        File sidecar = new File(OUTPUT_DIR, base + ".json");
        Files.writeString(sidecar.toPath(), """
                {"file":"%s","zoom":%d,"tilesOk":%d,"tilesTotal":%d,"center":[%s,%s],"corners":[[%s,%s],[%s,%s],[%s,%s],[%s,%s]]}
                """.formatted(jpg.getName(), zoom, ok.get(), tiles.size(), lon, lat,
                topLeft.lng(), topLeft.lat(), topRight.lng(), topRight.lat(),
                bottomRight.lng(), bottomRight.lat(), bottomLeft.lng(), bottomLeft.lat()));
        return result;
    }

    private BufferedImage download(int z, int col, int row) {
        String url = urlTemplate.replace("{z}", "" + z).replace("{x}", "" + col).replace("{y}", "" + row);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("User-Agent", UA)
                .timeout(Duration.ofSeconds(20)).GET().build();
        // Retry a few times: the tile service occasionally throttles or hiccups,
        // which would otherwise leave black squares in the stitched image.
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                HttpResponse<byte[]> resp = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
                if (resp.statusCode() == 200) {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(resp.body()));
                    if (img != null) {
                        return img;
                    }
                }
            } catch (Exception e) {
                // try again
            }
            try {
                Thread.sleep(200L * (attempt + 1));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null; // genuinely missing/failed -> that part stays black
    }

    private record TileImage(int col, int row, BufferedImage image) {
    }

    /** Standard XYZ Web Mercator (EPSG:3857) grid. */
    public static class WebMercatorGrid implements TileGrid {
        @Override
        public int colFor(double lon, double lat, int z) {
            return (int) Math.floor((lon + 180) / 360 * (1 << z));
        }

        @Override
        public int rowFor(double lon, double lat, int z) {
            double latRad = Math.toRadians(lat);
            return (int) Math.floor((1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI) / 2 * (1 << z));
        }

        @Override
        public double tileSpanMeters(int z, double lat) {
            return 40075016.686 * Math.cos(Math.toRadians(lat)) / (1 << z);
        }

        @Override
        public LngLat cornerLonLat(int col, int row, int z) {
            double lon = col / (double) (1 << z) * 360 - 180;
            double a = Math.PI * (1 - 2.0 * row / (1 << z));
            double lat = Math.toDegrees(Math.atan(Math.sinh(a)));
            return new LngLat(lon, lat);
        }
    }

    /**
     * The Finnish national EPSG:3067 (ETRS-TM35FIN) tile grid as used by
     * kartat.kapsi.fi and MML: top-left origin at (-548576, 8388608) meters and
     * resolution {@code 8192 / 2^z} meters/pixel.
     */
    public static class Tm35finGrid implements TileGrid {
        private static final double ORIGIN_E = -548576.0;
        private static final double ORIGIN_N = 8388608.0;

        private final CoordinateTransform toTm35;
        private final CoordinateTransform toWgs84;

        public Tm35finGrid() {
            CRSFactory f = new CRSFactory();
            CoordinateReferenceSystem wgs84 = f.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");
            CoordinateReferenceSystem tm35 = f.createFromParameters("EPSG:3067",
                    "+proj=utm +zone=35 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs");
            toTm35 = new BasicCoordinateTransform(wgs84, tm35);
            toWgs84 = new BasicCoordinateTransform(tm35, wgs84);
        }

        private double resolution(int z) {
            return 8192.0 / (1 << z);
        }

        private double span(int z) {
            return TILE_SIZE * resolution(z);
        }

        private ProjCoordinate toTm(double lon, double lat) {
            return toTm35.transform(new ProjCoordinate(lon, lat), new ProjCoordinate());
        }

        @Override
        public int colFor(double lon, double lat, int z) {
            return (int) Math.floor((toTm(lon, lat).x - ORIGIN_E) / span(z));
        }

        @Override
        public int rowFor(double lon, double lat, int z) {
            return (int) Math.floor((ORIGIN_N - toTm(lon, lat).y) / span(z));
        }

        @Override
        public double tileSpanMeters(int z, double lat) {
            return span(z);
        }

        @Override
        public LngLat cornerLonLat(int col, int row, int z) {
            double e = ORIGIN_E + col * span(z);
            double n = ORIGIN_N - row * span(z);
            ProjCoordinate ll = toWgs84.transform(new ProjCoordinate(e, n), new ProjCoordinate());
            return new LngLat(ll.x, ll.y);
        }
    }

    /**
     * Standalone runner: stitches a kapsi orthophoto around the test coordinate and
     * prints the saved file and its location.
     */
    public static void main(String[] args) throws Exception {
        double lat = 60.0880962, lon = 22.0826187;
        TileStitcher stitcher = new TileStitcher(OrthophotoGrabber.ORTHO_TEMPLATE, new Tm35finGrid());
        Result r = stitcher.stitch(lon, lat, OrthophotoGrabber.ZOOM, OrthophotoGrabber.RADIUS_M);
        System.out.println("Saved: " + r.file().getAbsolutePath() + " (" + r.tilesOk() + "/" + r.tilesTotal() + " tiles)");
        System.out.println("Corners: " + r.topLeft() + " " + r.topRight() + " " + r.bottomRight() + " " + r.bottomLeft());
    }
}
