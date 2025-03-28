package org.vaadin.addons.maplibre;

import com.vaadin.flow.function.SerializableConsumer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

public class PointField extends AbstractFeatureField<Point> implements Marker.DragEndListener {
    private Point point;
    private Marker marker;
    private SerializableConsumer<Marker> markerFormatter;

    public PointField(String label) {
        this();
        setLabel(label);
    }

    public PointField() {
    }

    @Override
    protected void reset() {
        super.reset();
        point = null;
        updateValue();
        marker.remove();
        marker = null;
    }

    @Override
    protected Point generateModelValue() {
        return point;
    }

    @Override
    protected void setPresentationValue(Point point) {
        this.point = point;
        getResetButton().setVisible(point != null);
        if (point == null) {
            // Click will insert marker
            if(marker != null) {
                marker.remove();
                marker = null;
            }
        } else {
            // edit existing
            if(marker == null) {
                createMarker(point);
            } else {
                marker.setPoint(point);
            }
            getMap().setCenter(point.getX(), point.getY());
        }
    }

    private void createMarker(Point point) {
        marker = getMap().addMarker(point)
                .addDragEndListener(this);
        if(markerFormatter != null) {
            markerFormatter.accept(marker);
        }
    }

    @Override
    protected void setMap(MapLibre map) {
        super.setMap(map);
        getMap().addMapClickListener(event -> {
            Coordinate coordinate = event.getCoordinate();
            assignPointFromCoordinate(coordinate);
            if(marker == null) {
                createMarker(point);
            } else  {
                marker.setPoint(point);
            }
            getResetButton().setVisible(true);
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

    public void setMarkerFormatter(SerializableConsumer<Marker> markerFormatter) {
        this.markerFormatter = markerFormatter;
        if(marker != null) {
            markerFormatter.accept(marker);
        }
    }
}
