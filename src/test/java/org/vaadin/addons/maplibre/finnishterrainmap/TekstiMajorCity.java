package org.vaadin.addons.maplibre.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class TekstiMajorCity extends SymbolLayerDefinition {

    public TekstiMajorCity() {
        setId("tekstimajorcity");
        setSource("mtk");
        setSourceLayer("paakaupungit");
        setPaint(new org.vaadin.addons.maplibre.dto.SymbolPaint(){{
            setTextColor("#000");
            setTextHaloColor("#fff");
            setTextHaloWidth(1);
        }});
        setLayout(new org.vaadin.addons.maplibre.dto.SymbolLayout(){{
            setTextField("{teksti}");
            setTextSize(16);
            setTextFont("Open Sans Semibold");
        }});
    }
}
