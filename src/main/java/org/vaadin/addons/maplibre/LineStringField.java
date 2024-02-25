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
    public LineStringField withStyleUrl(String styleUrl) {
        super.withStyleUrl(styleUrl);
        this.drawControl.addGeometryChangeListener(e -> {
            GeometryCollection geom = e.getGeom();
            lineString = (LineString) geom.getGeometryN(0);
            updateValue();
        });
        return this;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if(lineString == null) {
            drawControl.setMode(DrawControl.DrawMode.DRAW_LINE_STRING);
        }
    }

    @Override
    protected LineString generateModelValue() {
        return lineString;
    }

    @Override
    protected void setPresentationValue(LineString lineString) {
        this.lineString = lineString;
        if (lineString == null) {
            // put into drawing mode
            drawControl.setMode(DrawControl.DrawMode.DRAW_POLYGON);
        } else {
            // edit existing
            drawControl.setGeometry(lineString);
            drawControl.directSelectFirst();
            map.fitBounds(lineString);
        }
    }
}
