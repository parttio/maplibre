package org.vaadin.addons.maplibre.finnishterrainmap;

import org.vaadin.addons.maplibre.dto.expressions.In;

import static org.vaadin.addons.maplibre.finnishterrainmap.Maastokartta.ROAD_OVERALL_BREAKPOINT;

public class Tie3A3BReuna extends BasicLine {

    public Tie3A3BReuna() {
        super("tieviiva-numeroidut", "#000000");
        setId(getClass().getName());
        setFilter(new In("kohdeluokka", Kohdeluokka.AUTOTIE_IIIA, Kohdeluokka.AUTOTIE_IIIB));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_EDGE_S);
        setMinZoom(ROAD_OVERALL_BREAKPOINT);
    }
}
