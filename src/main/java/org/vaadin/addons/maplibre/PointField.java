package org.vaadin.addons.maplibre;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

public class PointField extends AbstractFeatureField<Point> implements Marker.DragEndListener {
    private Point point;
    private Marker marker;

    public PointField(String label) {
        this();
        setLabel(label);
    }

    public PointField() {
    }

    @Override
    protected Point generateModelValue() {
        return point;
    }

    @Override
    protected void setPresentationValue(Point point) {
        this.point = point;
        if (point == null) {
            // Click will insert marker
        } else {
            // edit existing
            if(marker == null) {
                marker = getMap().addMarker(point)
                        .addDragEndListener(this);
            } else {
                marker.setPoint(point);
            }
            getMap().setCenter(point.getX(), point.getY());
        }
    }

    @Override
    protected void setMap(MapLibre map) {
        super.setMap(map);
        getMap().addMapClickListener(event -> {
            Coordinate coordinate = event.getPoint();
            assignPointFromCoordinate(coordinate);
            if(marker == null) {
                marker = getMap().addMarker(point)
                        .addDragEndListener(this);
            } else  {
                marker.setPoint(point);
            }
            updateValue();
        });
    }

    private void assignPointFromCoordinate(Coordinate coordinate) {
        point = MapLibre.gf.createPoint(coordinate);
    }

    @Override
    public void dragEnd(Coordinate coordinate) {
        assignPointFromCoordinate(coordinate);
        updateValue();
    }
}
