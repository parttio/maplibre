package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import in.virit.color.Color;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Route
@JavaScript("https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-draw/v1.4.3/mapbox-gl-draw.js")
//@StyleSheet("https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-draw/v1.4.2/mapbox-gl-draw.css")
public class RawDrawingTests extends VVerticalLayout {
    private final MapLibre map;

    public RawDrawingTests() {
        add(new RichText().withMarkDown("""
                # Drawing geometries with mapbox-gl-draw
                        
                Although MapLibre was forked quite a quile ago, the
                mapbox-gl-draw plugin seems to be fairly compatible still.      
                """));
        try {
            map = new MapLibre(new URI("https://demotiles.maplibre.org/style.json"));
            map.setWidthFull();
            withExpanded(map);

            add(new VHorizontalLayout(
                    new VButton("Draw polygon (CTRL-P)", e -> {
                        drawPolygon().thenAccept(polygon -> {
                            map.addFillLayer(polygon, new FillPaint(NamedColor.RED, 0.3));
                        });
                    }).withClickShortcut(Key.KEY_P, KeyModifier.CONTROL),
                    new VButton("Draw linestring (CTRL-L)", e -> {
                        drawLineString().thenAccept(geom -> {
                            map.addLineLayer(geom, new LinePaint(NamedColor.BLUE, 1.0));
                        });
                    }).withClickShortcut(Key.KEY_L, KeyModifier.CONTROL))
            );

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Polygon> drawPolygon() {
        var f = new CompletableFuture<Polygon>();
        map.js("""
                    // Velocity template magic :-)
                    #set ( $dollar = "$")
                    
                    if(!this.draw) {
                        this.draw = new MapboxDraw({
                            displayControlsDefault: false,
                            controls: {}
                        });
                        map.addControl(this.draw);
                    }
                    this.draw.changeMode("draw_polygon");
                    // TODO figure out a good way to do this
                    map._canvasContainer.style.cursor ="crosshair";
                    
                    return new Promise(resolveGeometry => {
                        map.once('draw.create', () => {
                            const data = this.draw.getAll();
                            const geoJsonToServer = JSON.stringify(data);
                            this.draw.trash();
                            map._canvasContainer.style.cursor = "";
                            resolveGeometry(geoJsonToServer);
                        });
                    });

                """, Collections.emptyMap()).then(String.class, geojson -> {
            try {
                GeometryCollection collection
                        = (GeometryCollection) new GeoJsonReader().read(geojson);
                Polygon polygon = (Polygon) collection.getGeometryN(0);
                f.complete(polygon);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        return f;
    }

    public CompletableFuture<LineString> drawLineString() {
        var f = new CompletableFuture<LineString>();
        map.js("""
                    // Velocity template magic :-)
                    #set ( $dollar = "$")
                    
                    if(!this.draw) {
                        this.draw = new MapboxDraw({
                            displayControlsDefault: false,
                            controls: {}
                        });
                        map.addControl(this.draw);
                    }
                    this.draw.changeMode("draw_line_string");
                    // TODO figure out a good way to do this
                    map._canvasContainer.style.cursor ="crosshair";
                    
                    return new Promise(resolveGeometry => {
                        map.once('draw.create', () => {
                            const data = this.draw.getAll();
                            const geoJsonToServer = JSON.stringify(data);
                            this.draw.trash();
                            map._canvasContainer.style.cursor = "";
                            resolveGeometry(geoJsonToServer);
                        });
                    });

                """, Collections.emptyMap()).then(String.class, geojson -> {
            try {
                GeometryCollection collection
                        = (GeometryCollection) new GeoJsonReader().read(geojson);
                LineString geom = (LineString) collection.getGeometryN(0);
                f.complete(geom);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        return f;
    }
}
