package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.dto.Projection;
import org.vaadin.firitin.components.RichText;

import java.net.URI;
import java.net.URISyntaxException;

@Route
public class NonInteractiveAkaStaticMap extends VerticalLayout {
    public NonInteractiveAkaStaticMap() {
        add(new RichText().withMarkDown("""
        # Basic Map
        
        Only show static map of fixed location, like an images. Panning etc disabled.
        
        """));
        MapLibre map = null;
        try {
            map = new MapLibre(new URI("https://demotiles.maplibre.org/style.json"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        map.setInteractive(false);

        map.setHeight("400px");
        map.setWidth("100%");
        map.setCenter(24.945831, 60.192059);
        map.setZoomLevel(3);
        add(map);
    }
}
