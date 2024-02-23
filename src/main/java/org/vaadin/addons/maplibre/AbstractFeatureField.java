package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.customfield.CustomField;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Polygon;

public abstract class AbstractFeatureField<T> extends CustomField<T> {
    DrawControl drawControl;
    MapLibre map;

    public AbstractFeatureField() {
    }

    public AbstractFeatureField(String label) {
        setLabel(label);
    }

    public AbstractFeatureField withStyleUrl(String styleUrl) {
        this.map = new MapLibre(styleUrl);
        this.drawControl = new DrawControl(map);
        add(map);
        return this;
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

}
