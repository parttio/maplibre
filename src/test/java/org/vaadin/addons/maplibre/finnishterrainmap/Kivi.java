package org.vaadin.addons.maplibre.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.LayerDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayout;

public class Kivi extends SymbolLayerDefinition {

    public Kivi() {
        setId("kivi");
        setSource("mtk");
        setSourceLayer("kivi");

        setLayout(new SymbolLayout(){{
            setTextField("T");
            setTextRotate(180);
            setTextFont("Open Sans Semibold");
        }});
    }
}
