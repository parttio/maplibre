package org.vaadin.addons.maplibre.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class TekstiKunnannimi extends SymbolLayerDefinition {

    public TekstiKunnannimi() {
        setId("tekstikunnannimi");
        setSource("mtk");
        setSourceLayer("paikannimi");
        setPaint(new org.vaadin.addons.maplibre.dto.SymbolPaint(){{
            setTextColor("#000");
            setTextHaloColor("#fff");
            setTextHaloWidth(1);
        }});
        setLayout(new org.vaadin.addons.maplibre.dto.SymbolLayout(){{
            setTextField("{teksti}");
            setTextSize(14);
            setTextFont("Open Sans Semibold");

//            setTextAllowOverlap(true);
        }});
        setFilter(
                new Equals("kohdeluokka", 48111)
        );
    }
}
