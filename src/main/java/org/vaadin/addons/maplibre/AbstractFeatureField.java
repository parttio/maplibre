package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.StyleSheet;

@StyleSheet("context://org/vaadin/addons/maplibre/maplibre-addon.css")
public abstract class AbstractFeatureField<T> extends CustomField<T> {
    private DrawControl drawControl;
    private MapLibre map;

    public AbstractFeatureField() {
    }

    public AbstractFeatureField(String label) {
        this();
        setLabel(label);
    }

    public AbstractFeatureField initWithBaseLayer(String styleUrl) {
        setMap(new MapLibre(styleUrl));
        return this;
    }

    public MapLibre getMap() {
        if(map == null) {
            setMap(new MapLibre());
        }
        return map;
    }

    protected void setMap(MapLibre map) {
        this.map = map;
        add(map);
        drawControl = null;
    }

    public DrawControl getDrawControl() {
        if(drawControl == null) {
            drawControl = new DrawControl(map);
        }
        return drawControl;
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        addClassName("maplibre-field-has-size");
        getMap().setHeightFull();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        getMap().setWidth(width);
    }

}
