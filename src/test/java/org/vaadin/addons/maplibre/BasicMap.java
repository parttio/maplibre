package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.vaadin.firitin.components.RichText;

import java.net.URI;
import java.net.URISyntaxException;

@Route
public class BasicMap extends VerticalLayout {
    public BasicMap() {
        add(new RichText().withMarkDown("""
        # Basic Map
        
        "Hello world" using MapLibre's demo style declaration.
        
        """));
        try {
            MapLibre map = new MapLibre(new URI("https://demotiles.maplibre.org/style.json"));
            map.setHeight("400px");
            map.setWidth("100%");
            map.setCenter(24.945831, 60.192059);
            map.setZoomLevel(3);
            add(map);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
