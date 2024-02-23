package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.customfield.CustomField;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

/**
 * TODO this was essentially copypasted from Polygonfield
 * extract common superclass.
 *
 */
public class LineStringField extends CustomField<LineString> {
    final MapLibre map;
    private final DrawControl drawControl;
    private LineString lineString;

    public LineStringField(String styleUrl) {
        this.map = new MapLibre(styleUrl);
        this.drawControl = new DrawControl(map);
        this.drawControl.addGeometryChangeListener(e -> {
            GeometryCollection geom = e.getGeom();
            lineString = (LineString) geom.getGeometryN(0);
            updateValue();
        });
        add(map);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        // TODO fix relative sizes
//        map.setHeightFull();
        map.setHeight("280px");
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        map.setWidth(width);
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
        }
    }
}
