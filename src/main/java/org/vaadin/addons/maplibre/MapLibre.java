package org.vaadin.addons.maplibre;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.server.VaadinService;
import elemental.json.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.parttio.vaadinjsloader.JSLoader;
import org.vaadin.addons.maplibre.dto.FlyToOptions;
import org.vaadin.addons.velocitycomponent.AbstractVelocityJsComponent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Java/Vaadin API for MapLibre GL JS.
 */
@Tag("div")
public class MapLibre extends AbstractVelocityJsComponent implements HasSize, HasStyle {

    static GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);

    private final HashMap<String, Layer> idToLayer = new HashMap<>();
    // base style as Json
    private String styleJson;
    // base style url
    private String styleUrl;
    private ArrayList<MoveEndListener> moveEndListeners;
    private Coordinate center;
    private Double zoomLevel;
    private HashMap<String, Runnable> jsCallbacks = new HashMap<>();
    private List<MapClickListener> mapClickListeners;
    private boolean initialized;
    private boolean detached;
    private LinkedList<Runnable> deferredJsCalls = new LinkedList<>();
    private Polygon lastKnownViewPort;

    public MapLibre() {

        MapLibreBaseMapProvider provider = null;
        try {
            Instantiator instantiator = VaadinService.getCurrent().getInstantiator();
            provider = instantiator.getOrCreate(MapLibreBaseMapProvider.class);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Error finding base map provider", e);
        }
        if(provider == null) {
            // No bean to configure found, check context (original hack)
            VaadinContext context = VaadinService.getCurrent().getContext();
            provider = context.getAttribute(MapLibreBaseMapProvider.class);
        }
        if (provider == null) {
            // this is probably never what actual people want, log warning?
            this.styleUrl = "https://demotiles.maplibre.org/style.json";
            return;
        }
        Object o = provider.provideBaseStyle();

        if (o instanceof String url) {
            styleUrl = url;
        } else if (o instanceof InputStream styleJson) {
            try {
                this.styleJson = IOUtils.toString(styleJson, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (o instanceof URI uri) {
            styleUrl = uri.toString();
        }
    }

    public MapLibre(URI styleUrl) {
        this.styleUrl = styleUrl.toString();
    }

    public MapLibre(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public MapLibre(InputStream styleJson) {
        try {
            this.styleJson = IOUtils.toString(styleJson, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        if (!initialized) {
            if(getWidth() == null && getMinWidth() == null) {
                setWidth("100%");
                setMinWidth("100px");
            }
            if(getHeight() == null && getMinHeight() == null) {
                setHeight("100%");
                setMinHeight("100px");
            }
            loadMapLibreJs();
            jsTemplate("org/vaadin/addons/maplibre/mapinit.js", Map.of(
                    "style", styleJson == null ? "null" : styleJson, // Map.of is not nullsafe :-(
                    "styleUrl", styleUrl == null ? "null" : styleUrl,
                    "setCenter", center != null,
                    "setZoom", zoomLevel != null
            ));
            initialized = true;
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        init();
        if (detached) {
            throw new IllegalStateException("Re-attaching old map not currently supported!");
        }
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        detached = true;
    }

    /**
     * Loads the MapLibre JS library to the host page.
     * Not using @JavaScript annotation, as not all users
     * necessarily want to use the unpkg.com CDN.
     *
     * <p>Override if you want for example
     * to load it from a local file instead of from unpkg.com.</p>
     */
    protected void loadMapLibreJs() {
        JSLoader.loadUnpkg(this, "maplibre-gl", "4.1.2", "dist/maplibre-gl.js", "dist/maplibre-gl.css");
    }

    public Double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
        if(initialized) {
            js("map.setZoom($this.zoomLevel);");
        }
    }

    /**
     * Returns the defined center of this map.
     * Note that user can most likely pan the map.
     * Use getViewPort() method to detect the current value
     * from the client side.
     *
     * @return the last set center of this map.
     * @deprecated Might contain outdated value, consider using getViewPort()
     */
    @Deprecated
    public Coordinate getCenter() {
        return center;
    }

    public void setCenter(Coordinate coordinate) {
        this.center = coordinate;
        if (initialized) {
            js("map.setCenter($GeoJsonHelper.toJs($this.center));");
        }
    }

    public void setCenter(Geometry geom) {
        setCenter(geom.getCentroid().getCoordinate());
    }

    public void setBearing(double bearing) {
        js("map.setBearing($bearing);", Map.of("bearing", bearing));
    }

    private void addSource(String name, Geometry geometry) {
        js("""
                    map.addSource('$name', {
                      'type': 'geojson',
                      'data': $GeoJsonHelper.toJs($geometry)
                    });
                """, Map.of("name", name, "geometry", geometry));
    }

    public LineLayer addLineLayer(LineString geometry, LinePaint linePaint) {
        String id = UUID.randomUUID().toString();
        addSource(id, geometry);
        return addLineLayer(id, id, null, linePaint, geometry);
    }

    public Layer addFillLayer(Polygon polygon, FillPaint style) {
        String id = UUID.randomUUID().toString();
        addSource(id, polygon);
        return addFillLayer(id, id, null, style, polygon);
    }

    public Layer addFillLayer(Geometry geom, FillPaint style) {
        String id = UUID.randomUUID().toString();
        addSource(id, geom);
        return addFillLayer(id, id, null, style, geom);
    }

    protected Layer addFillLayer(String name, String source, String sourceLayer, FillPaint paintJson, Geometry geom) {
        if (sourceLayer == null) {
            sourceLayer = "";
        } else {
            sourceLayer = "'source-layer': '%s',".formatted(sourceLayer);
        }
        js("""
                    map.addLayer({
                      'id': '%s',
                      'type': 'fill',
                      'source': '%s',
                      %s
                      'layout': {},
                      'paint': %s
                    });
                """.formatted(name, source, sourceLayer, paintJson), Collections.emptyMap());
        return new Layer(this, name, geom);
    }

    /**
     * Adds a new layer only on the client sided
     *
     * @param name        the name of the new layer
     * @param source      the source id
     * @param sourceLayer the source layer
     * @param paint       the paint for the features
     * @return Layer handle (e.g. to remove the layer)
     */
    public LineLayer addLineLayer(String name, String source, String sourceLayer, LinePaint paint) {
        return addLineLayer(name, source, sourceLayer, paint, null);
    }

    protected LineLayer addLineLayer(String name, String source, String sourceLayer, LinePaint paint, Geometry geom) {
        if (sourceLayer == null) {
            sourceLayer = "";
        } else {
            sourceLayer = "'source-layer': '%s',".formatted(sourceLayer);
        }
        js("""
                    map.addLayer({
                      'id': '$name',
                      'type': 'line',
                      'source': '$source',
                       $sourceLayer
                      'layout': {
                        'line-join': 'round',
                        'line-cap': 'round'
                      },
                      'paint': $paint
                    });
                """, Map.of("name", name, "source", source, "sourceLayer", sourceLayer, "paint", paint));

        return new LineLayer(this, name, geom);
    }

    public void removeLayer(Layer layer) {
        if (layer instanceof Marker m) {
            js("""
                        component.markers['$id'].remove();
                    """, Map.of("id", m.id));
        } else {
            js("""
                        map.removeLayer('$id');
                        map.removeSource('$id');
                    """, Map.of("id", layer.id));
        }
        idToLayer.remove(layer.id);
    }

    public void addSource(String name, String sourceDeclarationJson) {
        js("""
                    map.addSource('$name', $sourceDeclarationJson);
                """, Map.of("name", name, "sourceDeclarationJson", sourceDeclarationJson));
    }

    public Marker addMarker(Point point) {
        return addMarker(point.getX(), point.getY());
    }

    public Marker addMarker(double x, double y) {
        String id = UUID.randomUUID().toString();
        js("""
                    component.markers = component.markers || {};
                    component.markers['$id'] = new maplibregl.Marker()
                            .setLngLat([$x, $y])
                            .addTo(map);
                    component.markers['$id'].getElement().id = '$id';
                """, Map.of("id", id, "x", x, "y", y));
        return new Marker(this, id, new Coordinate(x, y));
    }

    @Override
    protected VelocityContext getVelocityContext() {
        VelocityContext velocityContext = super.getVelocityContext();
        velocityContext.put("GeoJsonHelper", GeoJsonHelper.class);
        return velocityContext;
    }

    public void setCenter(double x, double y) {
        setCenter(new Coordinate(x, y));
    }

    public void fitTo(Geometry geom, double padding) {
        Envelope envelope = geom.getEnvelopeInternal();
        fitTo(envelope, padding);
    }

    protected void fitTo(Envelope envelope, double padding) {
        fitTo("""
                    const bounds = new maplibregl.LngLatBounds(
                    [%s, %s], [%s, %s]);;
                    map.fitBounds(bounds, {padding: %s});
                """.formatted(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY(), padding));
    }

    private void fitTo(String envelope) {
        js(envelope);
    }

    public void flyTo(FlyToOptions flyToOptions) {
        js("map.flyTo(%s)".formatted(flyToOptions));
    }

    public void flyTo(double x, double y, Double zoom) {
        js("""
                    const opts = {
                        center: [%s, %s]                    
                    }
                    const z = %s;
                    if(z != null) {
                        opts.zoom = z;                   
                    }
                    map.flyTo(opts);
                """.formatted(x, y, zoom));
    }

    /**
     * Executes given JS in the context of the map component,
     * either right away, or right after initial loading is done.
     *
     * @param js the JS to execute, map and component variables are initialized automatically.
     * @return
     */
    protected PendingJavaScriptResult js(String js, Map<String, Object> variables) {
        init();
        return velocityJs("""
                    const map = this.map;
                    const component = this;
                    const action = () => {
                        %s
                    };
                    if(!this.styleloaded) {
                        map.on('load', action);
                    } else {
                        return action();
                    }
                """.formatted(js), variables);
    }

    protected PendingJavaScriptResult js(String js) {
        return js(js, Collections.emptyMap());
    }

    public void flyTo(Geometry geometry, double zoomLevel) {
        Point centroid = geometry.getCentroid();
        flyTo(centroid.getX(), centroid.getY(), zoomLevel);
    }

    public void flyTo(Geometry geometry) {
        Point centroid = geometry.getCentroid();
        flyTo(centroid.getX(), centroid.getY(), null);
    }

    String registerJsCallback(Runnable r) {
        String id = UUID.randomUUID().toString();
        jsCallbacks.put(id, r);
        return id;
    }

    void deregisterJsCallback(String id) {
        jsCallbacks.remove(id);
    }

    @ClientCallable
    private void jsCallback(String cbId) {
        jsCallbacks.get(cbId).run();
    }

    void registerLayer(String id, Layer layer) {
        idToLayer.put(id, layer);
    }

    public void addMapClickListener(MapClickListener listener) {
        if (mapClickListeners == null) {
            mapClickListeners = new ArrayList<>();

            js("""
                    map.on("click", e => {
                        var evt = new Event("map-click");
                        const features = map.queryRenderedFeatures(e.point)
                        if(features[0]) {
                            evt.featureId = features[0].layer.id;
                        }
                        evt.lngLat = JSON.stringify(e.lngLat);
                        evt.point = JSON.stringify(e.point);
                        component.dispatchEvent(evt);
                    });
                                
                    """);

            getElement().addEventListener("map-click", domEvent -> {
                        MapClickEvent mapClickEvent = new MapClickEvent(domEvent);
                        for (MapClickListener l : mapClickListeners) {
                            l.onClick(mapClickEvent);
                        }
                    }).addEventData("event.lngLat")
                    .addEventData("event.point")
                    .addEventData("event.featureId");

        }
        mapClickListeners.add(listener);
    }

    public void fitBounds(Geometry geometry) {
        String geojson = new GeoJsonWriter().write(geometry.getEnvelope().getBoundary());
        js("""
                const bbox = JSON.parse('$bbox');
                const b = new maplibregl.LngLatBounds(bbox.coordinates[0],bbox.coordinates[1]);
                bbox.coordinates.forEach(c => {
                    b.extend([c[0],c[1]]);                    
                });
                map.fitBounds(b, {padding: 20});
                """, Map.of("bbox", geojson));
    }

    public void addMoveEndListener(MoveEndListener listener) {
        if (moveEndListeners == null) {
            moveEndListeners = new ArrayList<>();
            js("""
                    map.on("moveend", e => {
                        var evt = new Event("map-moveend");
                        evt.viewport = component.getViewPort();
                        evt.zoom = map.getZoom();
                        component.dispatchEvent(evt);
                    });
                    """);

            getElement().addEventListener("map-moveend", domEvent -> {
                        MoveEndEvent event = new MoveEndEvent(domEvent);
                        this.zoomLevel = event.getZoomLevel();
                        this.center = event.getViewPort().center.getCoordinate();
                        this.lastKnownViewPort = event.getViewPort().getBounds();
                        for (MoveEndListener l : moveEndListeners) {
                            l.onMove(event);
                        }
                    })
                    .addEventData("event.viewport")
                    .addEventData("event.zoom")
                    .debounce(150); // resizing may cause a ton of events, do a bit of debouncing
        }
        moveEndListeners.add(listener);
    }

    /**
     * Detects current view port details.
     *
     * @return viewport details
     */
    public CompletableFuture<ViewPort> getViewPort() {
        var res = new CompletableFuture<ViewPort>();
        getElement().callJsFunction("getViewPort")
                .then(JsonObject.class,
                        jso -> res.complete(ViewPort.of(jso)));
        return res;
    }

    public void fitToContent() {
        List<Geometry> geometries = new ArrayList<>();
        idToLayer.values().forEach(layer ->
                geometries.add(layer.getGeometry()));
        if (geometries.size() > 0) {
            try {
                Envelope env = geometries.get(0).getEnvelopeInternal();
                for (Geometry g : geometries) {
                    if (g != null) {
                        env.expandToInclude(g.getEnvelopeInternal());
                    }
                }
                fitTo(env, 20);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    public void removeAll() {
        ArrayList<Layer> layers = new ArrayList<>(this.idToLayer.values());
        layers.forEach(l -> removeLayer(l));
    }

    /**
     * Removes all current styles and layers and inits the map with given style URL
     *
     * @param styleUrl the styleUrl
     */
    public void setStyle(String styleUrl) {
        removeAll();
        js("""
                map.setStyle("$style");
                """, Map.of("style", styleUrl));
    }

    public interface MoveEndListener {
        void onMove(MoveEndEvent event);
    }

    public interface MapClickListener {

        public void onClick(MapClickEvent event);

    }

    public record ViewPort(Point southWest, Point northEast, Point center, double bearing, double pitch) {


        public static ViewPort of(JsonObject o) {
            var southWest = gf.createPoint(new Coordinate(
                    o.getObject("sw").getNumber("lng"),
                    o.getObject("sw").getNumber("lat")));
            double nelat = o.getObject("ne").getNumber("lat");
            double nelng = o.getObject("ne").getNumber("lng");
            var northEast = gf.createPoint(new Coordinate(nelng, nelat));
            double clat = o.getObject("c").getNumber("lat");
            double clng = o.getObject("c").getNumber("lng");
            return new ViewPort(southWest, northEast, gf.createPoint(new Coordinate(clng, clat)), o.getNumber("bearing"), o.getNumber("pitch"));
        }

        public Polygon getBounds() {
            // 4326
            // TODO this will most likely now not be perfect if bearing/pitch
            // is used, would need some math...
            Coordinate[] shell = new Coordinate[5];
            shell[0] = southWest.getCoordinate();
            shell[4] = southWest.getCoordinate();
            shell[2] = northEast.getCoordinate();
            shell[1] = new Coordinate(southWest.getX(), northEast.getY());
            shell[3] = new Coordinate(northEast.getX(), southWest.getY());
            return gf.createPolygon(shell);

        }

    }

    public class MoveEndEvent {

        ViewPort viewPort;
        double zoomLevel;

        public MoveEndEvent(DomEvent domEvent) {
            this.viewPort = ViewPort.of(domEvent.getEventData().getObject("event.viewport"));
            this.zoomLevel = domEvent.getEventData().getNumber("event.zoom");
            MapLibre.this.zoomLevel = this.zoomLevel;
        }

        public ViewPort getViewPort() {
            return viewPort;
        }

        public double getZoomLevel() {
            return zoomLevel;
        }

        @Override
        public String toString() {
            return "MoveEndEvent{" +
                    "viewPort=" + viewPort +
                    '}';
        }
    }

    public class MapClickEvent {
        private final Coordinate coordinate;
        private final Coordinate pixelCoordinate;
        private Layer layer;

        public MapClickEvent(DomEvent domEvent) {
            if (domEvent.getEventData().hasKey("event.featureId")) {
                String fId = domEvent.getEventData().getString("event.featureId");
                this.layer = idToLayer.get(fId);
            }
            try {
                LngLatRecord ll = AbstractKebabCasedDto.mapper.readValue(
                        domEvent.getEventData().getString("event.lngLat"), LngLatRecord.class);
                PointRecord p = AbstractKebabCasedDto.mapper.readValue(
                        domEvent.getEventData().getString("event.point"), PointRecord.class);
                this.coordinate = new Coordinate(ll.lng, ll.lat);
                this.pixelCoordinate = new Coordinate(p.x, p.y);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

        public Point getPoint() {
            return MapLibre.gf.createPoint(coordinate);
        }

        public Layer getLayer() {
            return layer;
        }

        record LngLatRecord(double lng, double lat) {
        }

        record PointRecord(double x, double y) {
        }
    }

    public Polygon getLastKnownViewPort() {
        return lastKnownViewPort;
    }
}
