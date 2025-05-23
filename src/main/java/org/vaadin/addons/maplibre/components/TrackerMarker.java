package org.vaadin.addons.maplibre.components;

import in.virit.color.Color;
import in.virit.color.NamedColor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.vaadin.addons.maplibre.LineLayer;
import org.vaadin.addons.maplibre.LinePaint;
import org.vaadin.addons.maplibre.MapLibre;
import org.vaadin.addons.maplibre.Marker;
import org.vaadin.addons.maplibre.dto.SymbolLayout;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

/**
 * A Marker with a tail, typically used to indicate the location of a moving object.
 *
 * Allows setting max age for points, so that they are removed from the map after a certain time.
 *
 * @deprecated A new component whose API (and presentation) is still in flux. Use at your own risk.
 */
@Deprecated(forRemoval = false)
public class TrackerMarker {

    protected String textPosX = "50%";
    protected String textPosY = "60%";
    protected String character = "";

    public static String svgArrowTemplate = """
    <svg width="30px" height="30px" viewBox="0 0 24 24" fill="none">
      <path
         d="M 3.16496,19.5025 10.5275,2.99281 c 1.518289,-2.88445928 1.511073,-2.88445928 2.945,0 L 20.835,19.5025 c 2.312172,4.237686 -0.8141,3.0474 -2.2019,2.3065 l -5.9037,-3.152 c -0.4592,-0.2452 -0.9996,-0.2452 -1.4588,0 L 5.36689,21.809 C 3.97914,22.5499 0.93718072,23.908912 3.16496,19.5025 Z"
         style="%s" />
        <text style="font-size: 10px;fill:white; font-weight:bold;" x="%s" y="%s" dominant-baseline="middle" text-anchor="middle">%s</text>
    </svg>
    """;

    private Marker marker;
    private LineLayer tail;

    record TrackPoint(Point point, Instant time, Integer rotation) {}

    static GeometryFactory gf = new GeometryFactory();

    private final MapLibre map;
    private Duration maxAge = Duration.ofMinutes(2);
    private Integer maxPoints;
    private Color color = NamedColor.BLUE;

    LinkedList<TrackPoint> points = new LinkedList<>();

    public TrackerMarker(MapLibre map) {
        this.map = map;
    }

    public void reset() {
        points.clear();
    }

    public void addPoint(double lon, double lat) {
        addPoint(new Coordinate(lon, lat));
    }

    public void addPoint(Coordinate point) {
        addPoint(point, Instant.now(), null);
    }

    public void addPoint(Coordinate coordinate, Instant time, Integer bearing) {
        if(bearing == null && points.size() > 1) {
            // calculate bearing of previous point
            TrackPoint last = points.getLast();

            double x1 = Math.toRadians(last.point.getX());
            double y1 = Math.toRadians(last.point.getY());
            double x2 = Math.toRadians(coordinate.x);
            double y2 = Math.toRadians(coordinate.y);


            double y = Math.sin(x2-x1) * Math.cos(y2);
            double x = Math.cos(y1)*Math.sin(y2) - Math.sin(y1)*Math.cos(y2)*Math.cos(x2-x1);
            double θ = Math.atan2(y, x);
            bearing = (int) Math.toDegrees(θ);
        }

        TrackPoint trackPoint = new TrackPoint(gf.createPoint(coordinate), time, bearing);
        points.add(trackPoint);
        int removed = 0;
        if(maxAge != null) {
            while(!points.isEmpty() && points.get(0).time().plus(maxAge).isBefore(Instant.now())) {
                points.remove(0);
                removed++;
            }
        }
        if(maxPoints != null) {
            while(points.size() > maxPoints) {
                points.remove(0);
                removed++;
            }
        }

        if(marker == null) {
            marker = map.addMarker(trackPoint.point());
            styleMarker();
        } else {
            marker.setPoint(trackPoint.point());
        }
        if(bearing != null) {
            marker.setRotation(bearing);
            marker.setRotationAlignment(SymbolLayout.RotationAlignment.map);
        }
        if(points.isEmpty()) {
            // TODO make the marker bit transparent to indicate that it's old
        }
        maintainTail(removed, coordinate);
        System.out.println("TM DEBUG: " + points.size() + " points, removed " + removed);
    }

    protected void styleMarker() {
        marker.setHtml(svgArrowTemplate.formatted(
                getDefaultSvgMarkerCss(),
                textPosX,
                textPosY,
                character
        ));
        marker.setOffset(0,0);
    }

    protected String getDefaultSvgMarkerCss() {
        return "fill: " + getColor().toString();
    }

    public Color getColor() {
        return color;
    }

    private void maintainTail(int remove, Coordinate coordinate) {
        if(tail == null) {
            if(points.size() > 1) {
                Coordinate[] coords = points.stream().map(TrackPoint::point).map(p -> p.getCoordinate()).toArray(i -> new Coordinate[i]);
                // TODO https://maplibre.org/maplibre-gl-js/docs/examples/line-gradient/ , dim the end of the tail with opacity
                tail = map.addLineLayer(gf.createLineString(coords), getLinePaint());
            }
        } else {
            tail.addCoordinates(remove, coordinate);
        }
    }

    protected LinePaint getLinePaint() {
        return new LinePaint(getColor()) {{
            setLineWidth(2.0);
        }};
    }

    public void setMaxAge(Duration maxAge) {
        this.maxAge = maxAge;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    /**
     * @return the marker if it has been added to the map
     */
    public Marker getMarker() {
        return marker;
    }

    public void setColor(Color color) {
        this.color = color;
        if(marker != null) {
            marker.setColor(color);
        }
    }

    /**
     * @param character a single character presented within the marker symbol
     */
    public void setCharacter(Character character) {
        this.character = character.toString();
    }
}
