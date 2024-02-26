package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.customfield.CustomField;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class PointField extends CustomField<Point> implements Marker.DragEndListener {
    MapLibre map;
    private Point point;
    private Marker marker;

    public PointField(String label) {
        this();
        setLabel(label);
    }

    public PointField() {
    }

    public void setHeight(String height) {
        super.setHeight(height);
        addClassName("maplibre-field-has-size");
        map.setHeightFull();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        map.setWidth(width);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
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
                marker = map.addMarker(point)
                        .addDragEndListener(this);
            } else {
                marker.setPoint(point);
            }
            map.setCenter(point.getX(), point.getY());
        }
    }

    public PointField withStyleUrl(String styleUrl) {
        this.map = new MapLibre(styleUrl);
        map.addMapClickListener(event -> {
            Coordinate coordinate = event.getPoint();
            assingPointFromCoordinate(coordinate);
            if(marker == null) {
                marker = map.addMarker(point)
                        .addDragEndListener(this);
            } else  {
                marker.setPoint(point);
            }
            updateValue();
        });
        add(map);
        return this;
    }

    private void assingPointFromCoordinate(Coordinate coordinate) {
        point = MapLibre.gf.createPoint(coordinate);
    }

    @Override
    public void dragEnd(Coordinate coordinate) {
        assingPointFromCoordinate(coordinate);
        updateValue();
    }
}
