package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@StyleSheet("context://org/vaadin/addons/maplibre/maplibre-addon.css")
public abstract class AbstractFeatureField<T> extends CustomField<T> {
    protected Runnable deferredTask;
    private DrawControl drawControl;
    private MapLibre map;

    private HorizontalLayout toolbar = new HorizontalLayout();
    private Button resetButton = new Button(VaadinIcon.TRASH.create(), e -> this.reset());
    private String stylesJson;

    protected void reset() {
        if(drawControl != null) {
            drawControl.clear();
        }
        getResetButton().setVisible(false);
    }

    public AbstractFeatureField() {
        toolbar.setSpacing(false);
        toolbar.add(resetButton);
        resetButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        resetButton.setTooltipText("Reset current geometry");
        resetButton.setVisible(false);
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
        if (map == null) {
            setMap(new MapLibre());
        }
        return map;
    }

    protected void setMap(MapLibre map) {
        toolbar.setPadding(false);
        toolbar.setClassName("maplibre-toolbar");
        add(toolbar);
        this.map = map;
        add(map);
        drawControl = null;
    }

    protected HorizontalLayout getToolbar() {
        return toolbar;
    }

    protected Button getResetButton() {
        return resetButton;
    }

    public DrawControl getDrawControl() {
        if (drawControl == null) {
            drawControl = new DrawControl(getMap(), stylesJson);
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

    protected void runAttached(Runnable task) {
        if(!isAttached()) {
            this.deferredTask = task;
        } else {
            task.run();
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        if(getWidth() == null) {
            setMinWidth("100px");
            setWidthFull();
        }

        if(attachEvent.isInitialAttach() && getValue() == null) {
            setPresentationValue(null);
        }
        super.onAttach(attachEvent);
        if(deferredTask != null) {
            deferredTask.run();
            deferredTask = null;
        }
    }

    public void setStylesJson(String stylesJson) {
        this.stylesJson = stylesJson;
    }
}
