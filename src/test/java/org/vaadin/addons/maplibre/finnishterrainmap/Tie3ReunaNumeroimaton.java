package org.vaadin.addons.maplibre.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.In;

import static org.vaadin.addons.maplibre.finnishterrainmap.Maastokartta.ROAD_OVERALL_BREAKPOINT;

public class Tie3ReunaNumeroimaton extends BasicLine {

    public Tie3ReunaNumeroimaton() {
        super("tieviiva-muut", "#000000");
        setId(getClass().getName());
        setFilter(new In("kohdeluokka", Kohdeluokka.AUTOTIE_IIIA, Kohdeluokka.AUTOTIE_IIIB));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_EDGE_S);
        setMinZoom(ROAD_OVERALL_BREAKPOINT);
    }
}
