package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class Dto {

    private String name = "Foobar";
    private LineString line;
    private Point point;
    private Polygon polygon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LineString getLine() {
        return line;
    }

    public void setLine(LineString line) {
        this.line = line;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }


    @Override
    public String toString() {
        return "Dto{" +
                "\n  name:'" + name + '\'' +
                "\n  line:" + line +
                "\n  point:" + point +
                "\n  polygon:" + polygon +
                "\n}";
    }
}
