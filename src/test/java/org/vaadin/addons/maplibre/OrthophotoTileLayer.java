package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.dto.RasterMapSource;
import org.vaadin.firitin.components.RichText;

import java.net.URI;

/**
 * Demonstrates raster tile backgrounds (XYZ/TMS/WMTS): the National Land Survey of
 * Finland orthophotos served as XYZ tiles (open data, via kartat.kapsi.fi) added
 * on top of a vector base map with {@link MapLibre#addRasterTileLayer}. The same
 * pattern works for any TMS/WMTS/XYZ tile service, e.g. satellite imagery layers.
 */
@Route
public class OrthophotoTileLayer extends VerticalLayout {

    static final String MAPTILER = "https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU";

    // Official National Land Survey of Finland orthophoto WMTS (EPSG:3857). The
    // api-key is the open data key already used by the other examples in this repo.
    // WMTS REST tiles are addressed as {z}/{y}/{x} (TileMatrix/TileRow/TileCol).
    static final String MML_ORTHO = "https://avoin-karttakuva.maanmittauslaitos.fi/avoin/wmts/1.0.0/"
            + "ortokuva/default/WGS84_Pseudo-Mercator/{z}/{y}/{x}.jpg"
            + "?api-key=95065def-f53b-44d6-b429-769c3d504e13";

    public OrthophotoTileLayer() throws Exception {
        add(new RichText().withMarkDown("""
                # Raster tile background

                Orthophotos from the National Land Survey of Finland, served as XYZ/WMTS raster tiles
                and added on top of a vector base map. The same pattern works for any TMS/WMTS/XYZ
                tile service (e.g. satellite imagery layers).
                """));

        MapLibre map = new MapLibre(new URI(MAPTILER));
        map.setCenter(22.2462, 60.1755); // Turku
        map.setZoomLevel(13);
        map.setWidth("100%");

        // The simplest case would be a one-liner:
        //   map.addRasterTileLayer("ortho", MML_ORTHO);
        // Here we configure the source a bit more (zoom range and attribution).
        RasterMapSource ortho = RasterMapSource.ofTiles(MML_ORTHO);
        ortho.setTileSize(256);
        ortho.setMaxzoom(18);
        ortho.setAttribution("© Maanmittauslaitos");
        map.addRasterTileLayer("ortho", ortho);

        addAndExpand(map);
        add(new OpacityField(map, "ortho", 1.0));
    }
}
