package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;

public class PolygonField extends AbstractFeatureField<Polygon> {
    private Polygon polygon;

    public PolygonField() {
    }

    public PolygonField(String label) {
        setLabel(label);
    }

    @Override
    public PolygonField withStyleUrl(String styleUrl) {
        super.withStyleUrl(styleUrl);
        this.drawControl.addGeometryChangeListener(e -> {
            GeometryCollection geom = e.getGeom();
            polygon = (Polygon) geom.getGeometryN(0);
            updateValue();
        });
        return this;
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
