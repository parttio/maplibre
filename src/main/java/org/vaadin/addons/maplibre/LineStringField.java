package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.customfield.CustomField;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public class LineStringField extends AbstractFeatureField<LineString> {
    private LineString lineString;

    public LineStringField() {
    }

    public LineStringField(String label) {
        super(label);
    }

    @Override
    protected void setMap(MapLibre map) {
        super.setMap(map);
        getDrawControl().addGeometryChangeListener(e -> {
            GeometryCollection geom = e.getGeom();
            lineString = (LineString) geom.getGeometryN(0);
            updateValue();
            getResetButton().setVisible(true);
        });
        getDrawControl().addModeChangeListener(e -> {
            if(lineString == null && e.getDrawMode() != DrawControl.DrawMode.DRAW_LINE_STRING) {
                getDrawControl().setMode(DrawControl.DrawMode.DRAW_LINE_STRING);
            }
        });
    }

    @Override
    protected void reset() {
        super.reset();
        lineString = null;
        getDrawControl().setMode(DrawControl.DrawMode.DRAW_LINE_STRING);
        updateValue();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(lineString == null) {
            getDrawControl().setMode(DrawControl.DrawMode.DRAW_LINE_STRING);
        }
    }

    @Override
    protected LineString generateModelValue() {
        return lineString;
    }

    @Override
    protected void setPresentationValue(LineString lineString) {
        this.lineString = lineString;
        getResetButton().setVisible(lineString != null);
        if (lineString == null) {
            // put into drawing mode
            getDrawControl().setMode(DrawControl.DrawMode.DRAW_POLYGON);
        } else {
            // edit existing
            getDrawControl().setGeometry(lineString);
            getDrawControl().directSelectFirst();
            getMap().fitBounds(lineString);
        }
    }
}
