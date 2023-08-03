package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.geolocation.Geolocation;

import java.net.URI;
import java.net.URISyntaxException;

@Route
public class OsmViaMapTiler extends VerticalLayout {
    private Marker yourPosition;

    public OsmViaMapTiler() {
        add(new RichText().withMarkDown("""
                # OpenStreetMap via MapTiler
                
                This example contains a simple vector basemap with a marker at Vaadin HQ and a random polygon plotted around it.
                Some action buttons to show basic API of the component.
                
                """));
        try {
            MapLibre map = new MapLibre(new URI("https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU"));
            map.setHeight("400px");
            map.setWidth("100%");
            map.addMarker(22.300, 60.452).withPopup("Hello from Vaadin!");

            Polygon polygon = (Polygon) new WKTReader().read("POLYGON((22.290 60.428, 22.310 60.429, 22.31 60.47, 22.28 60.47, 22.290 60.428))");

            map.addFillLayer(polygon, new FillPaint("red", 0.2));

            map.setCenter(22.300, 60.452);
            map.setZoomLevel(13);
            add(map);

            Button b = new Button("Zoom to content");
            b.addClickListener(e -> {
                Geometry g = polygon;
                if(yourPosition != null) {
                    g = g.union(yourPosition.getGeometry());
                }
                map.fitTo(g, 0.01);
            });
            Button seeWorld = new Button("See the world (flyTo(0,0,0)");
            seeWorld.addClickListener(e -> {
                map.flyTo(0,0,0);
            });
            Button plotYourself = new Button("Plot yourself");
            plotYourself.addClickListener(e -> {
                Geolocation.getCurrentPosition(position -> {
                    yourPosition = map.addMarker(position.getCoords().getLongitude(), position.getCoords().getLatitude());
                    map.flyTo(yourPosition.getGeometry(), 13);
                }, error -> {
                    System.out.println("Error: ");
                });
            });
            add(new HorizontalLayout(b, seeWorld, plotYourself));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
