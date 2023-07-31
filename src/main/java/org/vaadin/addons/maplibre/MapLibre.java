package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import org.apache.commons.io.IOUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * A Java/Vaadin API for MapLibre GL JS.
 */
@JavaScript("https://unpkg.com/maplibre-gl@latest/dist/maplibre-gl.js")
@StyleSheet("https://unpkg.com/maplibre-gl@latest/dist/maplibre-gl.css")
public class MapLibre extends Div {

    private Coordinate center = new Coordinate(0, 0);
    private int zoomLevel = 15;

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

    private void init(String style, String styleUrl) {
        setId("map");
        setWidth("800px");
        setHeight("500px");

        try {
            getElement().executeJs(IOUtils.toString(getClass().getResourceAsStream("maplibre-vaadin-connector.js"), Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getElement().executeJs("this.init(%s,'%s',%s,%s)".formatted(style, styleUrl, toJs(getCenter()), getZoomLevel()));
    }

    private int getZoomLevel() {
        return 15;
    }

    private Coordinate getCenter() {
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

    private static String toJs(Coordinate coord) {
        return "[" + coord.getX() + "," + coord.getY() + "]";
    }

    private void addSource(String name, Geometry geometry) {
        GeoJsonWriter writer = new GeoJsonWriter();
        writer.setEncodeCRS(false);
        js("""
            map.addSource('%s', {
              'type': 'geojson',
              'data': %s
            });
        """.formatted(name, writer.write(geometry)));
    }

    public Layer addLineLayer(Geometry geometry, String styleJson) {
        String id = UUID.randomUUID().toString();
        addSource(id, geometry);
        return addLineLayer(id, id, null, styleJson, geometry);
    }

    public Layer addFillLayer(Polygon polygon, String styleJson) {
        String id = UUID.randomUUID().toString();
        addSource(id, polygon);
        return addFillLayer(id, id, null, styleJson, polygon);
    }

    protected Layer addFillLayer(String name, String source, String sourceLayer, String paintJson, Geometry geom) {
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
        """.formatted(name, source, sourceLayer, paintJson));
        return new Layer(this, name, geom);
    }

     protected Layer addLineLayer(String name, String source, String sourceLayer, String paintJson, Geometry geom) {
        if(sourceLayer == null) {
            sourceLayer = "";
        }
        js("component.myAddLineLayer('%s','%s','%s',%s);".formatted(name, source, sourceLayer, paintJson));
        return new Layer(this, name, geom);
    }

    public void removeLayer(Layer layer) {
        if (layer instanceof Marker m) {
            js("""
                    component.markers['%s'].remove();
            """.formatted(m.id));
        } else {
            js("""
                map.removeLayer('%s');
                map.removeSource('%s');
            """.formatted(layer.id, layer.id));
        }
    }

    public void addSource(String name, String sourceDeclarationJson) {
        js("""
            map.addSource('%s', %s);
        """.formatted(name, sourceDeclarationJson));
    }

    public Marker addMarker(double x, double y) {
        String id = UUID.randomUUID().toString();
        js("""
            component.markers = component.markers || {};
            component.markers['%s'] = new maplibregl.Marker()
                    .setLngLat([%s, %s])
                    .addTo(map);
        """.formatted(id, x, y));
        return new Marker(this, id, new Coordinate(x, y));
    }

    public void setCenter(double x, double y) {
        this.center = new Coordinate(x, y);
        js("map.setCenter(%s);".formatted(toJs(center)));
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        getElement().executeJs("this.map.setZoom(%s);".formatted(zoomLevel));
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

    private String toJs(Geometry envelopeInternal) {
        GeoJsonWriter writer = new GeoJsonWriter();
        writer.setEncodeCRS(false);
        String written = writer.write(envelopeInternal);
        return written;
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
     * @param js the JS to execute, map & component variables are initialized automatically.
     */
    private void js(String js) {
        getElement().executeJs("""
            const map = this.map;
            const component = this;
            const action = () => {
                %s
            };
            if(!this.styleloaded) {
                map.on('load', action);
            } else {
                action();
            }
        """.formatted(js));
    }

    public void flyTo(Geometry geometry, int i) {
        Point centroid = geometry.getCentroid();
        flyTo(centroid.getX(), centroid.getY(), i);
    }
}
