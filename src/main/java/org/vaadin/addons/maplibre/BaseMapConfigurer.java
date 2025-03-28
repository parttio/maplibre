package org.vaadin.addons.maplibre;

/**
 * Used configure the default base map styles application wide.
 */
public interface BaseMapConfigurer {

    /**
     * @return the base map style as String, URI or InputStream
     */
    void configure(MapLibre map);

}
