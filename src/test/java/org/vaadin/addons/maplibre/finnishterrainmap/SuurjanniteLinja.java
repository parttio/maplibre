package org.vaadin.addons.maplibre.finnishterrainmap;

public class SuurjanniteLinja extends BasicLine {
    public SuurjanniteLinja() {
        super("sahkolinja", "#666");
        getPaint().setLineWidth(0.75);
        setMinZoom(13);
        // TODO
    }
}
