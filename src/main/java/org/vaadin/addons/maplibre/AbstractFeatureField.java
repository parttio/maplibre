package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.StyleUtil;
import com.vaadin.flow.function.SerializableConsumer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.parttio.vaadinjsloader.JSLoader;
import org.vaadin.firitin.util.VStyleUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class AbstractFeatureField<T> extends CustomField<T> {
    protected Runnable deferredTask;
    private DrawControl drawControl;
    private MapLibre map;
    private String baseLayerStyleUrl;

    private HorizontalLayout toolbar = new HorizontalLayout();
    private Button resetButton = new Button(VaadinIcon.TRASH.create(), e -> this.reset());
    private String stylesJson;
    private boolean allowJsInput = true;

    private SerializableConsumer<MapLibre> mapDecorator = null;

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
        this.baseLayerStyleUrl = styleUrl;
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
        if(mapDecorator != null) {
            mapDecorator.accept(map);
        }
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

    /**
     * Sets a decorator that can be used for example to programmatically draw static
     * features below the edited one.
     *
     * @param mapDecorator the decorator
     */
    public void setMapDecorator(SerializableConsumer<MapLibre> mapDecorator) {
        this.mapDecorator = mapDecorator;
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

        if(!attachEvent.isInitialAttach() && map != null) {
            // Re-attaching (e.g. dialog re-opened): replace with a fresh map
            remove(map);
            MapLibre freshMap = baseLayerStyleUrl != null
                    ? new MapLibre(baseLayerStyleUrl)
                    : new MapLibre();
            setMap(freshMap);
            setPresentationValue(getValue());
        } else if(attachEvent.isInitialAttach() && getValue() == null) {
            setPresentationValue(null);
        }
        super.onAttach(attachEvent);
        if(deferredTask != null) {
            deferredTask.run();
            deferredTask = null;
        }

        injectCrusialStyles();

    }

    protected void injectCrusialStyles() {
        // Users, including myself are too stupid to configure Spring Security together
        // with Vaadin, so better NEVER use StyleSheet, but programatically inject styles
        // Originally had this annotation on this class: @StyleSheet("context://org/vaadin/addons/maplibre/maplibre-addon.css")
        // Too bad I need to now add Viritin as dependency...
        try {
            String stylesheet = new String(getClass().getResourceAsStream("/META-INF/resources/org/vaadin/addons/maplibre/maplibre-addon.css").readAllBytes(), StandardCharsets.UTF_8);
            VStyleUtil.injectAsFirst(stylesheet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStylesJson(String stylesJson) {
        this.stylesJson = stylesJson;
    }

    public boolean isAllowJsInput() {
        return allowJsInput;
    }

    /**
     * Makes it possible to submit input also via JS API. Handy for E2E testing with e.g. Playwright, as otherwise using
     * geoinput fields is almost impossible.
     *
     * TODO figure out if should be enabled via system level config/vaadin dev mode only
     * @param allowJsInput true if geojson input via js api should be allowed.
     */
    public void setAllowJsInput(boolean allowJsInput) {
        this.allowJsInput = allowJsInput;
    }

    @ClientCallable
    void inputGeoJson(String geojson) {
        if(allowJsInput) {
            try {
                Geometry geometryViaJs = new GeoJsonReader().read(geojson);
                setModelValue((T) geometryViaJs, true);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
