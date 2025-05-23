package org.vaadin.addons.maplibre.finnishterrainmap;

import in.virit.color.RgbColor;
import org.vaadin.addons.maplibre.dto.expressions.All;
import org.vaadin.addons.maplibre.dto.expressions.Equals;

public class Polku extends BasicLine {

    public Polku() {
        super("tieviiva-muut", new RgbColor(14, 14, 14, 1));
        setId("ajopolku");

        setFilter(new All(
                new Equals("kohdeluokka", 12316)
        ));
        getPaint().setLineWidth(RoadWidthConstants.ROAD_WIDTH_S);
        getPaint().setLineDasharray(9, 2);
        setMinZoom(12);
    }
}
