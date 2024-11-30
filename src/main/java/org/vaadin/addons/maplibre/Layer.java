package org.vaadin.addons.maplibre;

public class Layer {
    final String id;
    final MapLibre map;

    public Layer(String id, MapLibre map) {
        this.id = id;
        this.map = map;
        map.registerLayer(id, this);
    }

    public void remove() {
        map.removeLayer(this);
    }
}
