package org.vaadin.addons.maplibre;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import org.apache.commons.io.IOUtils;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import in.virit.color.RgbColor;
import org.vaadin.addons.maplibre.dto.VectorMapSource;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Route
public class FinnishOpenDataExample extends VerticalLayout {
    private Marker marker;

    Pre details = new Pre();

    class FinnishPropertyAreaMap extends MapLibre {

        List<Layer> highlightedLayers = new ArrayList<>();

        public FinnishPropertyAreaMap() throws IOException, URISyntaxException {

            // use OSM tiles via maptiler.com as base layer
            super(new URI("https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU"));
            // This could be used as well:
            //https://avoin-karttakuva.maanmittauslaitos.fi/vectortiles/stylejson/v20/hobby.json?api-key=95065def-f53b-44d6-b429-769c3d504e13

            setCenter(22.2462, 60.1755);
            setZoomLevel(15);
            setHeight("50vh");

            // add Finnish estate data as name source
//            addSource("kiinteisto-avoin", "{'type':'vector', 'url':\"https://avoin-karttakuva.maanmittauslaitos.fi/kiinteisto-avoin/v3/kiinteistojaotus/WGS84_Pseudo-Mercator/tilejson.json?api-key=95065def-f53b-44d6-b429-769c3d504e13\"}");

            addSource("kiinteisto-avoin", new VectorMapSource("https://avoin-karttakuva.maanmittauslaitos.fi/kiinteisto-avoin/v3/kiinteistojaotus/WGS84_Pseudo-Mercator/tilejson.json?api-key=95065def-f53b-44d6-b429-769c3d504e13"));
            addLineLayer("rajat", "kiinteisto-avoin", "KiinteistorajanSijaintitiedot", new LinePaint(new RgbColor(255,0,0,1), 2.0), null);
            addFillLayer("alueet", "kiinteisto-avoin", "PalstanSijaintitiedot", new FillPaint(new RgbColor(255,0,0,0.015), null), null);

            // Add a client side click listener to the map, to get the property id
            // from the base layer and pass that to server side method
            js("""
                map.on('click', (e) => {
                        const features = map.queryRenderedFeatures(e.point);
                        features.forEach(f => {
                            if(f.properties['kiinteistotunnus']) {
                                component.${server}.handleData(f.properties['kiinteistotunnus']);
                            }
                        });
                    });
                """, Map.of("server", "$server"));

        }

        // Client side calls this when clicked on the map and
        // a property id is found
        @ClientCallable
        public void handleData(String propertyId) {
            // Remove already highlighted layers
            highlightedLayers.forEach(layer -> layer.remove());
            highlightedLayers.clear();
            // Highlight the selected property
            Notification.show("You clicked on property: " + propertyId);
            try {
                // Now get full details base on the id from https://avoin-paikkatieto.maanmittauslaitos.fi/kiinteisto-avoin/simple-features/v3/collections/PalstanSijaintitiedot/items/ID
                String url = "https://avoin-paikkatieto.maanmittauslaitos.fi/kiinteisto-avoin/simple-features/v3/collections/PalstanSijaintitiedot/items?kiinteistotunnus=" + propertyId + "&api-key=95065def-f53b-44d6-b429-769c3d504e13";
                String responsegeojson = IOUtils.toString(URI.create(url),Charset.defaultCharset());
                // Parse JTS geometry from the response,
                // "native Java" API for this and many other Java GIS libraries,
                // such as Hibernate Spatial
                GeometryCollection geom = (GeometryCollection) new GeoJsonReader().read(responsegeojson);
                for(int i = 0; i < geom.getNumGeometries(); i++) {
                    Polygon polygon = (Polygon) geom.getGeometryN(i);
                    highlightedLayers.add(addFillLayer(polygon, new FillPaint(NamedColor.RED, 0.5)));
                }
                // Show the raw (but formatted) details in a pre element
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(responsegeojson);
                String formatted = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
                details.setText(formatted);

            } catch (ParseException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public FinnishOpenDataExample() throws IOException, URISyntaxException {
        add(new RichText().withMarkDown("""
        # Open Data example
        
        This example and consume some open data by Finland.
        Initially we render the [Finnish property boundaries as vector tiles](https://www.maanmittauslaitos.fi/karttakuvapalvelu/tekninen-kuvaus-vektoritiilet) if zoomed close enough.
        The clicked feature is inspected to find the property id
        using a custom JS snippet.
        
        The ID is then pushed to the server that makes a REST call
        to get full details and highlights the property with a layer with red color.
        
        """));

        MapLibre map = new FinnishPropertyAreaMap();
        details.setWidth("400px");
        details.getStyle().setOverflow(Style.Overflow.AUTO);
        add(new VHorizontalLayout().withExpanded(
                map,
                new VerticalLayout(
                        new H3("REST call answer:"),
                        details)
                )
        );

    }
}
