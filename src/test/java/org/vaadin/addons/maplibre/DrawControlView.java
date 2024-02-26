package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.mutable.MutableObject;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route
public class DrawControlView extends VVerticalLayout {

    public DrawControlView() {
        MapLibre map = new MapLibre("https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU");
        DrawControl drawControl = new DrawControl(map);
        addAndExpand(map);

        var toolbar = new VHorizontalLayout();

        // TODO disabled items in Viritin
        // EnumSelect<DrawControl.DrawMode> modeSelect = new EnumSelect<>(DrawControl.DrawMode.class);
        RadioButtonGroup<DrawControl.DrawMode> modeSelect = new RadioButtonGroup<>();
        modeSelect.setItems(DrawControl.DrawMode.values());
        modeSelect.setItemEnabledProvider(item -> {
            if (item == DrawControl.DrawMode.DIRECT_SELECT) {
                return false; // needs id
            }
            return true;
        });
        modeSelect.addValueChangeListener(e -> {
            if (e.isFromClient())
                drawControl.setMode(e.getValue());
        });
        modeSelect.setValue(DrawControl.DrawMode.SIMPLE_SELECT);

        // TODO figure out what is wrong with selecting drawing
        // modeSelect.setValue(DrawControl.DrawMode.DRAW_POLYGON);
        // drawControl.setMode(DrawControl.DrawMode.DRAW_POLYGON);
        // TODO figure out by setting mode with keyboard shortcut fails

        toolbar.add(modeSelect);

        drawControl.addModeChangeListener(e -> {
            modeSelect.setValue(e.getDrawMode());
        });

        toolbar.add(new VButton("Show geometries", buttonClickEvent -> {
            drawControl.getAll().thenAccept(geometryCollection -> {
                Notification.show("Drawn geometries:" + geometryCollection.toText());
            });

        }));

        toolbar.add(new VButton("Add a & edit", buttonClickEvent -> {

            WKTReader wktReader = new WKTReader();
            try {
                Polygon polygon = (Polygon) wktReader.read("""
                        POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10),
                        (20 30, 35 35, 30 20, 20 30))
                        """);
                drawControl.setGeometry(polygon);
                drawControl.directSelectFirst();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }));
        toolbar.add(new VButton("Cut hole to polygon", buttonClickEvent -> {

            WKTReader wktReader = new WKTReader();
            drawControl.getAll().thenAccept(geometryCollection -> {
                // Make sure we have a polygon to cut...
                boolean containsGeom = false;
                for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
                    if (geometryCollection.getGeometryN(i) instanceof Polygon) {
                        containsGeom = true;
                        break;
                    }
                }
                if (!containsGeom) {
                    try {
                        Polygon polygon = (Polygon) wktReader.read("""
                                POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10))
                                """);
                        drawControl.setGeometry(polygon);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                // Now the actual dragin logic...
                drawControl.setMode(DrawControl.DrawMode.DRAW_POLYGON);
                DrawControl.DrawEventListener<DrawControl.GeometryChangeEvent>
                        listener = new DrawControl.DrawEventListener<>() {
                    @Override
                    public void onEvent(DrawControl.GeometryChangeEvent event) {
                        // TODO "once" listeners for better lambda support
                        drawControl.removeGeometryChangeListener(this);

                        GeometryCollection gc = event.getGeom();
                        Polygon hole = (Polygon) gc.getGeometryN(gc.getNumGeometries() - 1);
                        Polygon polygon = null;
                        // find first polygon that contains the hole
                        for (int i = 0; i < gc.getNumGeometries(); i++) {
                            Geometry g = gc.getGeometryN(i);
                            if (g instanceof Polygon p) {
                                if (p.contains(hole)) {
                                    polygon = p;
                                    break;
                                }
                            }
                        }
                        if (polygon == null) {
                            Notification.show("Hole needs to be within another polygon! Added now as a new polygon.");
                        } else {
                            Geometry cutPolygon = polygon.symDifference(hole);
                            if (cutPolygon instanceof MultiPolygon) {
                                Notification.show("This now became a multipolygon as wholes where not inside the exterior ring or wholes overlap! Further whole cutting witht this logic will fail.");
                            }
                            // re-create collection replace old polygon with the cutted
                            Geometry[] geoms = new Geometry[gc.getNumGeometries() - 1];
                            for (int i = 0; i < geoms.length; i++) {
                                Geometry g = gc.getGeometryN(i);
                                if (g == polygon) {
                                    geoms[i] = cutPolygon;
                                } else {
                                    geoms[i] = g;
                                }
                            }
                            drawControl.setGeometry(MapLibre.gf.createGeometryCollection(geoms));
                        }
                    }
                };

                drawControl.addGeometryChangeListener(listener);

            });
        }));

        add(toolbar);
    }
}
