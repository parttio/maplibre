package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.page.PendingJavaScriptResult;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.parttio.vaadinjsloader.JSLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A "raw" Java API for mapbox-gl-draw control, that is
 * compatible with maplibre. Actual fields can then utilize
 * this API.
 */
public class DrawControl {

    public class ModeChangeEvent extends EventObject{
        private final DrawMode drawMode;

        public ModeChangeEvent(DrawMode drawMode) {
            super(DrawControl.this);
            this.drawMode = drawMode;
        }

        public DrawMode getDrawMode() {
            return drawMode;
        }
    }

    @FunctionalInterface
    public interface DrawEventListener<T extends EventObject> {
        public void onEvent(T event);
    }

    private List<DrawEventListener<ModeChangeEvent>> modeChangeListeners;
    public void addModeChangeListener(DrawEventListener<ModeChangeEvent> listener) {
        if(modeChangeListeners == null) {
            modeChangeListeners = new ArrayList<>();
            map.getElement().addEventListener("modechange", e-> {
                ModeChangeEvent event = new ModeChangeEvent(DrawMode.valueOf(e.getEventData().getString("event.mode").toUpperCase()));
                modeChangeListeners.forEach(l -> l.onEvent(event));
            }).addEventData("event.mode");
        }
        modeChangeListeners.add(listener);
    }

    public enum DrawMode {
        SIMPLE_SELECT,
        DIRECT_SELECT,
        DRAW_LINE_STRING,
        DRAW_POLYGON,
        DRAW_POINT
    }

    private final MapLibre map;
    private final String id = "draw" + UUID.randomUUID().toString().substring(0,4);

    private DrawMode mode = DrawMode.SIMPLE_SELECT;

    public DrawControl(MapLibre map) {
        this.map = map;
        injectScript();
        map.addAttachListener(e -> {
            doInit();
        });
        if(map.isAttached()) {
            doInit();
        }
    }

    private void doInit() {
        map.js("""
        const drawOptions = {
            defaultMode : "%s",
            displayControlsDefault: false
        };
        const id = "%s";
        const draw = new MapboxDraw(drawOptions);
        map[id] = draw;
        map.addControl(draw);
        
        map.on("draw.modechange", e => {
        
            // TODO figure out a proper way for cursors
            if(e.mode == "direct_select") {
                map._canvasContainer.style.cursor = "default";
            } else {
                map._canvasContainer.style.cursor = "";
            }
        
            const evt = new Event("modechange");
            evt.mode = e.mode;
            component.dispatchEvent(evt);
        });
        """.formatted(mode.toString().toLowerCase(), id));
    }

    public void setMode(DrawMode mode) {
        this.mode = mode;
        if(map.isAttached()) {
            js("""
            const mode = "$mode";
            draw.changeMode(mode);
            if(mode.indexOf("draw") != -1 ) {
                map._canvasContainer.style.cursor = "default";
            }
            """, Map.of("mode", mode.toString().toLowerCase()));
        } else {
            js("""
            if($mode.indexOf("draw") != -1 ) {
                map._canvasContainer.style.cursor = "default";
            }
            """, Map.of("mode", mode.toString().toLowerCase()));

        }
    }

    protected void injectScript() {
        JSLoader.loadFiles(map, "https://api.mapbox.com/mapbox-gl-js/plugins/mapbox-gl-draw/v1.4.3/mapbox-gl-draw.js", "mapbox-gl-draw", "v1.4.3");
    }

    private PendingJavaScriptResult js(String js, Map args) {
        js = "const draw = map['%s'];\n".formatted(id) + js;
        return map.js(js,args);
    }

    public CompletableFuture<GeometryCollection> getAll() {
        var v = new CompletableFuture<GeometryCollection>();
        js("""
        const all = draw.getAll();
        return JSON.stringify(all);
        """, Collections.emptyMap()).then(String.class, str -> {
            try {
                GeometryCollection geom = (GeometryCollection) new GeoJsonReader().read(str);
                v.complete(geom);
            } catch (ParseException e) {
                v.completeExceptionally(e);
            }
        });
        return v;
    }

}
