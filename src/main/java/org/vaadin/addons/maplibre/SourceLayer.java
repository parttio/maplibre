package org.vaadin.addons.maplibre;

/**
 * A handle to a vector layer in the map that draws source from a separately defined
 * source. As you can't directly modify the actual features in this layer,
 * you can essentially only remove this layers using this handle.
 */
public class SourceLayer extends Layer {
    SourceLayer(String id, MapLibre map) {
        super(id, map);
    }
}
