package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;


@Route
public class FieldsTests extends VVerticalLayout {


    public FieldsTests() {
        LineStringField lineStringField = new LineStringField();
        lineStringField.setStylesJson("""
                [
                    // ACTIVE (being drawn)
                    // line stroke
                    {
                        "id": "gl-draw-line",
                        "type": "line",
                        "filter": ["all", ["==", "$type", "LineString"], ["!=", "mode", "static"]],
                        "layout": {
                          "line-cap": "round",
                          "line-join": "round"
                        },
                        "paint": {
                          "line-color": "#D20C0C",
                          "line-dasharray": [0.2, 2],
                          "line-width": 2
                        }
                    },
                    // polygon fill
                    {
                      "id": "gl-draw-polygon-fill",
                      "type": "fill",
                      "filter": ["all", ["==", "$type", "Polygon"], ["!=", "mode", "static"]],
                      "paint": {
                        "fill-color": "#D20C0C",
                        "fill-outline-color": "#D20C0C",
                        "fill-opacity": 0.1
                      }
                    },
                    // polygon mid points
                    {
                      'id': 'gl-draw-polygon-midpoint',
                      'type': 'circle',
                      'filter': ['all',
                        ['==', '$type', 'Point'],
                        ['==', 'meta', 'midpoint']],
                      'paint': {
                        'circle-radius': 3,
                        'circle-color': '#fbb03b'
                      }
                    },
                    // polygon outline stroke
                    // This doesn't style the first edge of the polygon, which uses the line stroke styling instead
                    {
                      "id": "gl-draw-polygon-stroke-active",
                      "type": "line",
                      "filter": ["all", ["==", "$type", "Polygon"], ["!=", "mode", "static"]],
                      "layout": {
                        "line-cap": "round",
                        "line-join": "round"
                      },
                      "paint": {
                        "line-color": "#D20C0C",
                        "line-dasharray": [0.2, 2],
                        "line-width": 2
                      }
                    },
                    // vertex point halos
                    {
                      "id": "gl-draw-polygon-and-line-vertex-halo-active",
                      "type": "circle",
                      "filter": ["all", ["==", "meta", "vertex"], ["==", "$type", "Point"], ["!=", "mode", "static"]],
                      "paint": {
                        "circle-radius": 5,
                        "circle-color": "#FFF"
                      }
                    },
                    // vertex points
                    {
                      "id": "gl-draw-polygon-and-line-vertex-active",
                      "type": "circle",
                      "filter": ["all", ["==", "meta", "vertex"], ["==", "$type", "Point"], ["!=", "mode", "static"]],
                      "paint": {
                        "circle-radius": 3,
                        "circle-color": "#D20C0C",
                      }
                    },
                        
                    // INACTIVE (static, already drawn)
                    // line stroke
                    {
                        "id": "gl-draw-line-static",
                        "type": "line",
                        "filter": ["all", ["==", "$type", "LineString"], ["==", "mode", "static"]],
                        "layout": {
                          "line-cap": "round",
                          "line-join": "round"
                        },
                        "paint": {
                          "line-color": "#000",
                          "line-width": 3
                        }
                    },
                    // polygon fill
                    {
                      "id": "gl-draw-polygon-fill-static",
                      "type": "fill",
                      "filter": ["all", ["==", "$type", "Polygon"], ["==", "mode", "static"]],
                      "paint": {
                        "fill-color": "#000",
                        "fill-outline-color": "#000",
                        "fill-opacity": 0.1
                      }
                    },
                    // polygon outline
                    {
                      "id": "gl-draw-polygon-stroke-static",
                      "type": "line",
                      "filter": ["all", ["==", "$type", "Polygon"], ["==", "mode", "static"]],
                      "layout": {
                        "line-cap": "round",
                        "line-join": "round"
                      },
                      "paint": {
                        "line-color": "#000",
                        "line-width": 3
                      }
                    }
                  ]
        """);
        lineStringField.setHeight("400px");
        lineStringField.setWidth("400px");
        add(lineStringField);
        try {
            LineString lineString = (LineString) new WKTReader().read("LINESTRING (30 10, 10 30, 40 40)");
//            lineStringField.setValue(lineString);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }

        add(new Button("add in dialog", e-> {
            new Dialog("Dialog") {{
                setMinHeight("300px");
                setMinWidth("300px");
                LineStringField lineStringField1 = new LineStringField();
                lineStringField1.setSizeFull();
//            lineStringField1.setHeight("400px");
//            lineStringField1.setWidth("400px");
                add(lineStringField1);
            }}.open();
        }));

    }

}
