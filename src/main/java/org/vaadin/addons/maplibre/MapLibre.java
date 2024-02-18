package org.vaadin.addons.maplibre;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import com.vaadin.flow.dom.DomEvent;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.parttio.vaadinjsloader.JSLoader;
import org.vaadin.addons.velocitycomponent.AbstractVelocityJsComponent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A Java/Vaadin API for MapLibre GL JS.
 */
@Tag("div")
public class MapLibre extends AbstractVelocityJsComponent implements HasSize, HasStyle {

    private Coordinate center = new Coordinate(0, 0);
    private int zoomLevel = 0;

    private final HashMap<String,Layer> idToLayer = new HashMap<>();

    public MapLibre(URI styleUrl) {
        init(null, styleUrl.toString());
    }

    public MapLibre(String styleUrl) {
        init(null, styleUrl);
    }

    public MapLibre(InputStream styleJson) {
        try {
            init(IOUtils.toString(styleJson, Charset.defaultCharset()), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init(String styleJson, String styleUrl) {
        loadMapLibreJs();
        setId("map");
        setWidth("800px");
        setHeight("500px");

        jsTemplate("org/vaadin/addons/maplibre/mapinit.js", Map.of(
                "style", styleJson == null ? "null" : styleJson, // Map.of is not nullsafe :-(
                "styleUrl", styleUrl == null ? "null" : styleUrl
        ));
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
        JSLoader.loadUnpkg(this, "maplibre-gl","latest", "dist/maplibre-gl.js","dist/maplibre-gl.css");
    }

    public Integer getZoomLevel() {
        return zoomLevel;
    }

    public Coordinate getCenter() {
        return center;
    }

    public String getMapStyle() {
        try {
            String s = IOUtils.resourceToString("/kiinteistojaotus-taustakartalla.json", Charset.defaultCharset());
            return s;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addSource(String name, Geometry geometry) {
        js("""
            map.addSource('$name', {
              'type': 'geojson',
              'data': $GeoJsonHelper.toJs($geometry)
            });
        """, Map.of("name", name, "geometry", geometry));
    }

    public Layer addLineLayer(Geometry geometry, LinePaint linePaint) {
        String id = UUID.randomUUID().toString();
        addSource(id, geometry);
        return addLineLayer(id, id, null, linePaint, geometry);
    }

    public Layer addFillLayer(Polygon polygon, FillPaint style) {
        String id = UUID.randomUUID().toString();
        addSource(id, polygon);
        return addFillLayer(id, id, null, style, polygon);
    }

    protected Layer addFillLayer(String name, String source, String sourceLayer, FillPaint paintJson, Geometry geom) {
        if(sourceLayer == null) {
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

     protected Layer addLineLayer(String name, String source, String sourceLayer, LinePaint paint, Geometry geom) {
        if(sourceLayer == null) {
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

         return new Layer(this, name, geom);
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

    public Marker addMarker(double x, double y) {
        String id = UUID.randomUUID().toString();
        js("""
            component.markers = component.markers || {};
            component.markers['$id'] = new maplibregl.Marker()
                    .setLngLat([$x, $y])
                    .addTo(map);
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
        this.center = new Coordinate(x, y);
        js("map.setCenter($GeoJsonHelper.toJs($this.center));");
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        js("map.setZoom($this.zoomLevel);");
    }

    public void fitTo(Geometry geom, double padding) {
        Envelope envelope = geom.getEnvelopeInternal();
        envelope.expandBy(padding);
        js("""
            const bounds = new maplibregl.LngLatBounds(
            [%s, %s], [%s, %s]);;
            map.fitBounds(bounds);
        """.formatted(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY()));
    }

    public void flyTo(double x, double y, double zoom) {
        js("""
            map.flyTo({
                center: [%s, %s],
                zoom: %s
            });
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

    protected void js(String js) {
        js(js, Collections.emptyMap());
    }

    public void flyTo(Geometry geometry, int i) {
        Point centroid = geometry.getCentroid();
        flyTo(centroid.getX(), centroid.getY(), i);
    }

    private HashMap<String, Runnable> jsCallbacks = new HashMap<>();

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


    public class MapClickEvent {
        private final Layer layer;
        private final Coordinate point;
        private final Coordinate pixelCoordinate;

        public Coordinate getPoint() {
            return point;
        }

        record LngLatRecord(double lng, double lat) {}
        record PointRecord(double x, double y) {}

        public MapClickEvent(DomEvent domEvent) {
            String fId = domEvent.getEventData().getString("event.featureId");
            this.layer = idToLayer.get(fId);
            try {
                LngLatRecord ll = AbstractKebabCasedDto.mapper.readValue(
                        domEvent.getEventData().getString("event.lngLat"), LngLatRecord.class);
                PointRecord p = AbstractKebabCasedDto.mapper.readValue(
                        domEvent.getEventData().getString("event.point"), PointRecord.class);
                this.point = new Coordinate(ll.lng, ll.lat);
                this.pixelCoordinate = new Coordinate(p.x,p.y);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public Layer getLayer() {
            return layer;
        }
    }
    public interface MapClickListener {

        public void onClick(MapClickEvent event);

    }

    private List<MapClickListener> mapClickListeners;

    public void addMapClickListener(MapClickListener listener) {
        if(mapClickListeners == null) {
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

    @ClientCallable
    private void _fireClick() {

    }


}
