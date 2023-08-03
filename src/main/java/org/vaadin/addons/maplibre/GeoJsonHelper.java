package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

public class GeoJsonHelper {
    static GeoJsonWriter writer = new GeoJsonWriter();

    static {
        writer.setEncodeCRS(false);
    }

    public static String toJs(Coordinate coord) {
        return "[" + coord.getX() + "," + coord.getY() + "]";
    }

    public static String toJs(org.locationtech.jts.geom.Geometry geometry) {
        return writer.write(geometry);
    }

}
