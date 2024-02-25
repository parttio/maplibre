package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.StyleSheet;

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
        // TODO figure out how to do this properly, can't be right...
        // make the custom field's input slot take all available space
        getElement().executeJs("""
            var el = this;
            el.shadowRoot.querySelector(".inputs-wrapper").style.flexGrow = 1;
        """);
        map.setHeightFull();
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        map.setWidth(width);
    }

}
