package org.vaadin.addons.maplibre;

/**
 * Used configure the default base map styles application wide.
 */
public interface BaseMapConfigurer {

    /**
     * @param map the MapLibre instance to configure
     */
    void configure(MapLibre map);

}
