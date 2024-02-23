package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.customfield.CustomField;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;

public class PolygonField extends CustomField<Polygon> {
    final MapLibre map;
    private final DrawControl drawControl;
    private Polygon polygon;

    public PolygonField(String styleUrl) {
        this.map = new MapLibre(styleUrl);
        this.drawControl = new DrawControl(map);
        this.drawControl.addGeometryChangeListener(e -> {
            GeometryCollection geom = e.getGeom();
            polygon = (Polygon) geom.getGeometryN(0);
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
        if(polygon == null) {
            drawControl.setMode(DrawControl.DrawMode.DRAW_POLYGON);
        }
    }

    @Override
    protected Polygon generateModelValue() {
        return polygon;
    }

    @Override
    protected void setPresentationValue(Polygon polygon) {
        this.polygon = polygon;
        if (polygon == null) {
            // put into drawing mode
            drawControl.setMode(DrawControl.DrawMode.DRAW_POLYGON);
        } else {
            // edit existing
            drawControl.setGeometry(polygon);
            drawControl.setMode(DrawControl.DrawMode.SIMPLE_SELECT);
            drawControl.directSelectFirst();
        }
    }
}
