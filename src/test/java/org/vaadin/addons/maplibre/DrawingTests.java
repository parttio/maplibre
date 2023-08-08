package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.vaadin.firitin.components.RichText;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Route
@JavaScript("https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-draw/v1.4.2/mapbox-gl-draw.js")
//@StyleSheet("https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-draw/v1.4.2/mapbox-gl-draw.css")
public class DrawingTests extends VerticalLayout {
    public DrawingTests() {
        add(new RichText().withMarkDown("""
        # Basic Map
        
        "Hello world" using MapLibre's demo style declaration.
        
        """));
        try {
            MapLibre map = new MapLibre(new URI("https://demotiles.maplibre.org/style.json"));
            map.setHeight("400px");
            map.setWidth("100%");
            add(map);

            add(new Button("Draw polygon", e-> {
                map.getElement().getStyle().setCursor("crosshair");
                map.js("""
                        // Velocity template magic :-)
                        #set ( $dollar = "$")
                                        
                        const draw = new MapboxDraw({
                            displayControlsDefault: false,
                            controls: {},
                            defaultMode: 'draw_polygon'
                        });
                        
                        map.addControl(draw);
                        // TODO figure out a good way to do this
                        map._canvasContainer.style.cursor ="crosshair";
                        
                        map.on('draw.create', updateArea);
                                
                        function updateArea(e) {
                            const data = draw.getAll();
                            $drawingTest.${dollar}server.updateArea(JSON.stringify(data));
                            map.removeControl(draw); // this still throws an exception, but looks good (drawn feature is removed).
                        }          
                        
                    """, Map.of("drawingTest", getElement()));
            }));


        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @ClientCallable
    public void updateArea(String data) {
        System.out.println(data);

        try {
            Geometry geometry = new GeoJsonReader().read(data);
            Notification.show("Area: " + geometry.toText());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }
}
