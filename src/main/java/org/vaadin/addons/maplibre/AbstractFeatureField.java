package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.StyleSheet;

@StyleSheet("context://org/vaadin/addons/maplibre/maplibre-addon.css")
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
        addClassName("maplibre-field-has-size");
        map.setHeightFull();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        map.setWidth(width);
    }

}
