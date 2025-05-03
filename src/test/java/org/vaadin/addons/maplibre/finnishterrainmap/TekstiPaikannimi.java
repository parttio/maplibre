package org.vaadin.addons.maplibre.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.expressions.Equals;
import org.vaadin.addons.maplibre.dto.expressions.NotEquals;

public class TekstiPaikannimi extends SymbolLayerDefinition {

    public TekstiPaikannimi() {
        setId(getClass().getSimpleName());
        setSource("mtk");
        setSourceLayer("paikannimi");
        setPaint(new org.vaadin.addons.maplibre.dto.SymbolPaint(){{
            setTextColor("#000");
            setTextHaloColor("#fff");
            setTextHaloWidth(1);
        }});
        setLayout(new org.vaadin.addons.maplibre.dto.SymbolLayout(){{
            setTextField("{teksti}");
            setTextSize(12);
            setTextFont("Open Sans Semibold");

//            setTextAllowOverlap(true);
        }});
        setFilter(
                new NotEquals("kohdeluokka", 48111)
        );
    }
}
