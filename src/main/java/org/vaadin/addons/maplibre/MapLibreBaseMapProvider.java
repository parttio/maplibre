package org.vaadin.addons.maplibre;

/**
 * Used configure the default base map styles application wide.
 */
public interface MapLibreBaseMapProvider {

    /**
     * @return the base map style as String, URI or InputStream
     */
    Object provideBaseStyle();

}
