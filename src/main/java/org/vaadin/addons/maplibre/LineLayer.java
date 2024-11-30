package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class LineLayer extends GeometryLayer {
    LineLayer(MapLibre map, String id, Geometry geometry) {
        super(map, id, geometry);
    }

    public void addCoordinates(int coordinatesToRemoveFromTheBeginning, Coordinate... coordinatesToAdd) {
        String newCoords = Arrays.stream(coordinatesToAdd).map(coord -> "[%s, %s]".formatted(coord.x, coord.y)).collect(Collectors.joining(",", "[", "]"));
        map.js("""
        const id = "$id";
        const newCoords = $newCoords;
        const toRemove = $toRemove;
        const geojson = map.getSource(id)._data;
        geojson.coordinates.push(...newCoords);
        geojson.coordinates.splice(0, toRemove);
        map.getSource(id).setData(geojson);
        """, Map.of("id", id, "newCoords", newCoords, "toRemove", coordinatesToRemoveFromTheBeginning));
    }
}
