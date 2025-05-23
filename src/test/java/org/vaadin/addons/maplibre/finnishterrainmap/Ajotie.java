package org.vaadin.addons.maplibre.finnishterrainmap;

import in.virit.color.RgbColor;
import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class Ajotie extends BasicLine {

    public Ajotie() {
        super("tieviiva-muut", new RgbColor(14, 14, 14, 1));
        setId("ajotie");

        setFilter(new Any(
                new Equals("kohdeluokka", Kohdeluokka.AJOTIE),
                new Equals("kohdeluokka", Kohdeluokka.AUTOTIE_IIA),
                new Equals("kohdeluokka", Kohdeluokka.AUTOTIE_IIB),
                new Equals("kohdeluokka", Kohdeluokka.AUTOTIE_IIIA),
                new Equals("kohdeluokka", Kohdeluokka.AUTOTIE_IIIB)
        ));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_WIDTH_S);
        setMinZoom(Maastokartta.ROAD_OVERALL_BREAKPOINT);
    }
}
