package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;

public class PolygonField extends AbstractFeatureField<Polygon> {
    private boolean cuttingHole;

    private boolean allowCuttingHoles = false;
    private Polygon polygon;

    Button cutHole = new Button(VaadinIcon.SCISSORS.create());

    public PolygonField() {
        getToolbar().add(cutHole);
        cutHole.setVisible(false);
        cutHole.addThemeVariants(ButtonVariant.LUMO_SMALL);
        cutHole.addClassName("maplibre-cut-hole");
        cutHole.setTooltipText("Cut a new whole to the current polygon.");
        cutHole.addClickListener(e -> {
            getDrawControl().setMode(DrawControl.DrawMode.DRAW_POLYGON);
            cuttingHole = true;
        });
    }

    public PolygonField(String label) {
        this();
        setLabel(label);
    }

    @Override
    protected void setMap(MapLibre map) {
        super.setMap(map);
        getDrawControl().addGeometryChangeListener(e -> {
            GeometryCollection geom = e.getGeom();
            if(cuttingHole) {
                cuttingHole = false;
                try {
                    Geometry hole = geom.getGeometryN(1);
                    polygon = (Polygon) geom.getGeometryN(0).symDifference(hole);
                } catch (Exception ex) {
                    Notification.show("Cutting hole failed, make sure it is withing the existing Polygon and doesn't conflict other holes.");
                }
                getDrawControl().setGeometry(polygon);
            } else {
                polygon = (Polygon) geom.getGeometryN(0);
                cutHole.setVisible(allowCuttingHoles && polygon != null);
                getResetButton().setVisible(polygon != null);
            }
            updateValue();
        });
        getDrawControl().addModeChangeListener(e -> {
            if(polygon == null && e.getDrawMode() != DrawControl.DrawMode.DRAW_POLYGON) {
                getDrawControl().setMode(DrawControl.DrawMode.DRAW_POLYGON);
            }

        });
    }

    public PolygonField withAllowCuttingHoles(boolean allow) {
        allowCuttingHoles = allow;
        return this;
    }

    @Override
    protected Polygon generateModelValue() {
        return polygon;
    }

    @Override
    protected void reset() {
        super.reset();
        getDrawControl().setMode(DrawControl.DrawMode.DRAW_POLYGON);
        cutHole.setVisible(false);
        polygon = null;
        updateValue();
    }

    @Override
    protected void setPresentationValue(Polygon polygon) {
        this.polygon = polygon;
        cutHole.setVisible(allowCuttingHoles && polygon != null);
        getResetButton().setVisible(polygon != null);
        runAttached(() -> {
            if (polygon == null) {
                // put into drawing mode
                getDrawControl().setMode(DrawControl.DrawMode.DRAW_POLYGON);
            } else {
                // edit existing
                getDrawControl().setGeometry(polygon);
                getDrawControl().setMode(DrawControl.DrawMode.SIMPLE_SELECT);
                getDrawControl().directSelectFirst();
                getMap().fitBounds(polygon);
            }
        });

    }

}
