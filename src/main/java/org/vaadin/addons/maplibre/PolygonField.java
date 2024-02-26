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
        add(cutHole);
        cutHole.setVisible(false);
        cutHole.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        cutHole.addClassName("maplibre-cut-hole");
        cutHole.setTooltipText("Cut a new whole to the current polygon.");
        cutHole.addClickListener(e -> {
            drawControl.setMode(DrawControl.DrawMode.DRAW_POLYGON);
            cuttingHole = true;
        });
    }

    public PolygonField(String label) {
        this();
        setLabel(label);
    }

    @Override
    public PolygonField withStyleUrl(String styleUrl) {
        super.withStyleUrl(styleUrl);
        this.drawControl.addGeometryChangeListener(e -> {
            GeometryCollection geom = e.getGeom();
            if(cuttingHole) {
                cuttingHole = false;
                try {
                    Geometry hole = geom.getGeometryN(1);
                    polygon = (Polygon) geom.getGeometryN(0).symDifference(hole);
                } catch (Exception ex) {
                    Notification.show("Cutting hole failed, make sure it is withing the existing Polygon and doesn't conflict other holes.");
                }
                drawControl.setGeometry(polygon);
            } else {
                polygon = (Polygon) geom.getGeometryN(0);
            }
            updateValue();
        });
        return this;
    }

    public PolygonField withAllowCuttingHoles(boolean allow) {
        allowCuttingHoles = allow;
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
        cutHole.setVisible(allowCuttingHoles && polygon != null);
        if (polygon == null) {
            // put into drawing mode
            drawControl.setMode(DrawControl.DrawMode.DRAW_POLYGON);
        } else {
            // edit existing
            drawControl.setGeometry(polygon);
            drawControl.setMode(DrawControl.DrawMode.SIMPLE_SELECT);
            drawControl.directSelectFirst();
            map.fitBounds(polygon);
        }
    }

}
