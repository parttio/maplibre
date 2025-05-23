package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.dto.CircleLayerDefinition;
import org.vaadin.addons.maplibre.dto.CirclePaint;
import org.vaadin.addons.maplibre.dto.FillLayerDefinition;
import org.vaadin.addons.maplibre.dto.LineLayerDefinition;
import in.virit.color.RgbColor;
import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayout;
import org.vaadin.addons.maplibre.dto.SymbolPaint;
import org.vaadin.addons.maplibre.dto.VectorMapSource;
import org.vaadin.addons.maplibre.dto.expressions.All;
import org.vaadin.addons.maplibre.dto.expressions.Any;
import org.vaadin.addons.maplibre.dto.expressions.CaseExpression;
import org.vaadin.addons.maplibre.dto.expressions.Equals;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;
import org.vaadin.addons.maplibre.dto.expressions.NotEquals;
import org.vaadin.addons.maplibre.dto.expressions.PropertyEquals;
import org.vaadin.addons.maplibre.dto.expressions.ZoomStep;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route
public class FinnishOpenDataOverTheSea extends VerticalLayout {
    private static final String MERIDATA = "meridata";

    private Marker marker;

    public FinnishOpenDataOverTheSea() throws IOException, URISyntaxException {
        add(new H1("Finnish Open Data over the sea"));

        MapLibre map = new FinnishTerrainMapWithSomeNauticalChartDataMap();
        addAndExpand(map);

    }

    class FinnishTerrainMapWithSomeNauticalChartDataMap extends MapLibre {

        public FinnishTerrainMapWithSomeNauticalChartDataMap() throws IOException, URISyntaxException {

            // The Vector-styles-nls.json file is a "pimped" version of the original "vector maastokartta" example by
            // National Land Survey of Finland. We'll use that as a base layer.
            super(FinnishTerrainMapWithSomeNauticalChartDataMap.class.getResourceAsStream("/vector-styles-nls.json"));

            setCenter(22.2462, 60.1755);
            setZoomLevel(11);
            setHeight("50vh");

            this.addMoveEndListener(event -> {
                Notification.show("Map moved to " + getCenter() + " zoom level " + getZoomLevel());
            });

            // This raw Vector Tile sources contains:
            // - Depth contours. areas and soundings by Traficom
            // - Fairways, beacons, etc. by Väylävirasto
            // Packaged together with GDAL & Tippecanoe
            // The service URL is open, but runs on a tiny server -> don't use it for anything else but testing
            // TODO re-package the data so that boyes, etc. are not dropped on smaller zoom levels
            addSource(MERIDATA, new VectorMapSource("https://fvtns.dokku1.parttio.org/services/meridata/"));

            // Now we can use a typed Java API to define the layers rather than raw JSON 😎
            addSourceLayer(new FillLayerDefinition() {{
                setId("shallow"); // This is optional, if not set, the ID is generated
                setSource(MERIDATA);
                setSourceLayer("DepthArea");
                // white with 20% opacity on top of the base maps blue color
                setPaint(new FillPaint("rgba(255, 255, 255, 0.2)"));
                // Show only on zoom level 11 and above
                setMinZoom(11);
                // DRVAL1 property in the feature is the depth range start in meters, -> 0 means shallow
                setFilter(new Equals("DRVAL1", "0"));
                // Just to test APIs, this is the same as above (with one rule), could e.g. add more depth areas
                setFilter(new Any(new Equals("DRVAL1", "0")));
            }});

            addSourceLayer(new LineLayerDefinition() {{
                setSource(MERIDATA);
                setSourceLayer("DepthContour");
                setPaint(new LinePaint(new RgbColor(109, 109, 184, 0.5), 1.0));
                setMinZoom(12);
            }});
            // Above is the same as below
            /*
            addLayer("""
                            {
                                "id": "dcontour",
                                "type": "line",
                                "source": "meridata",
                                "source-layer": "DepthContour",
                                "paint": {
                                    "line-color": "rgba(109, 109, 184, 0.5)"
                                },
                                "minzoom": 12
                            }
                    """);
        */

            // Render the number of the depth contour lines
            addSourceLayer(new SymbolLayerDefinition() {{
                setId("dcontour-label");
                setSource(MERIDATA);
                setSourceLayer("DepthContour");
                setMinZoom(12);
                setFilter(new Equals("$type", "LineString"));

                setLayout(new SymbolLayout() {{
                    setSymbolPlacement(SymbolPlacement.lineCenter);
                    setTextField("{VALDCO} ");
                    setTextFont("Open Sans Semibold");
                    setTextSize(Interpolate
                            .exponential(1.4)
                            .zoom(
                                    new ZoomStep(10, 9),
                                    new ZoomStep(20, 14)
                            )
                    );
                }});

                setPaint(new SymbolPaint() {{
                    setTextColor("rgba(100, 100, 200, 1)");
                    setTextHaloColor("rgba(255, 255, 255, 0.43)");
                    setTextHaloWidth(2);
                }});

            }});

            addSourceLayer(new SymbolLayerDefinition() {{
                setId("sounding-point");
                setSource(MERIDATA);
                setSourceLayer("Sounding");
                setMinZoom(12);
                setLayout(new SymbolLayout() {{
                    setSymbolPlacement(SymbolPlacement.point);
                    setTextField("{DEPTH}");
                    setTextFont("Open Sans Semibold");
                    setTextSize(Interpolate
                            .exponential(1.4)
                            .zoom(
                                    new ZoomStep(10, 8),
                                    new ZoomStep(20, 14)
                            )
                    );
                    setTextRotationAlignment(SymbolLayout.TextRotationAlignment.map);
                    setTextTransform(SymbolLayout.TextTransform.uppercase);
                }});
                setPaint(new SymbolPaint() {{
                    setTextColor("rgba(200, 100, 200, 0.7)");
                }});
            }});

            addSourceLayer(new FillLayerDefinition() {{
                setId("vaylaalueet");
                setSource(MERIDATA);
                setSourceLayer("vaylaalueet");
                setPaint(new FillPaint("rgba(117, 255, 57, 0.2)"));
                setMinZoom(11);
            }});

            addSourceLayer(new LineLayerDefinition() {{
                setId("navigointilinjat");
                setSource(MERIDATA);
                setSourceLayer("navigointilinjat");
                setPaint(new LinePaint(new RgbColor(0, 200, 0, 0.9)));
                setMinZoom(11);
            }});

            addSourceLayer(new CircleLayerDefinition() {{
                setId("muut-merkit");
                setSource(MERIDATA);
                setSourceLayer("turvalaitteet");
                setMinZoom(12);
                // Filter out some symbols that are handled by some other layer
                var alreadyHandledSymbols = new ArrayList<>(LateralMarks.all);
                alreadyHandledSymbols.addAll(CardinalMarks.allMarks);
                setFilter(new All(NotEquals.anyValue("symboli",alreadyHandledSymbols)));

                setPaint(new CirclePaint() {{
                    setCircleStrokeColor("#000080");
                    setCircleStrokeWidth(2);
                    setCircleOpacity(1);
                    setCircleColor(
                            new CaseExpression(
                                    // default value for "other marks" bluegreenish
                                    "rgba(0, 200, 200, 1)",
                                    // Make "Kummelit" (sea cairns?) white
                                    PropertyEquals.anyValue("symboli", "rgba(255, 255, 255, 1)", "w", "2"))
                    );
                    setCircleRadius(Interpolate
                            .exponential(1.55)
                            .zoom(
                                    new ZoomStep(6, 0.75),
                                    new ZoomStep(20, 30)
                            )
                    );
                }});


            }});

            addSourceLayer(new CardinalMarks());
            addSourceLayer(new LateralMarks());

        }

        private class CardinalMarks extends SymbolLayerDefinition {
            // Väylävirasto uses different symbols for cardinal marks
            static final List<String> eastMarks = Arrays.asList("k", "l", "Q");
            static final List<String> westMarks = Arrays.asList("j", "O", "i");
            static final List<String> northMarks = Arrays.asList("K", "f", "9");
            static final List<String> southMarks = Arrays.asList("A", "h", "M", "g", "N");
            static final List<String> allMarks = new ArrayList<>(){{
                addAll(eastMarks);
                addAll(westMarks);
                addAll(southMarks);
                addAll(northMarks);
            }};

            {
            setId("kardinaalit");
            setSource(MERIDATA);
            setSourceLayer("turvalaitteet");
            setMinZoom(12);
            setLayout(new SymbolLayout() {{

                setIconSize(0.5);
                setIconImage("pin");
                // Plan is to rotate icons based on their nominal direction
                // With static rotation, all cardinal marks would be added separately
                setIconRotate(90);
                // Now lets use case expression to rotate them instead
                setIconRotate(new CaseExpression(
                        // default, TODO figure out if some symbol type is missing for all both south there is 3!!
                        0,
                        PropertyEquals.anyValue("symboli", -90, eastMarks),
                        PropertyEquals.anyValue("symboli", 90, westMarks),
                        PropertyEquals.anyValue("symboli", 180, northMarks),
                        PropertyEquals.anyValue("symboli", 0, southMarks)
                ));
                setIconRotationAlignment(SymbolLayout.TextRotationAlignment.map);
                /*
                setTextField(new CaseExpression(
                    new Equals("symboli", "j"), "W",
                    new Equals("symboli", "O"), "W",
                    new Equals("symboli", "h"), "S",
                    new Equals("symboli", "k"), "E",
                    new Equals("symboli", "i"), "w",
                    new Equals("symboli", "l"), "E",
                    new Equals("symboli", "Q"), "E",
                    new Equals("symboli", "h"), "S",
                    new Equals("symboli", "M"), "S",
                    new Equals("symboli", "K"), "N",
                    new Equals("symboli", "f"), "N",
                    "N"
                ));

                 */
                setTextSize(12);

                //setTextLineHeight(1.5);
            }});
            setPaint(new SymbolPaint() {{
                setTextColor("rgba(221, 0, 179, 1)");
            }});
            setFilter(new Any(
                    Equals.anyValue("symboli", allMarks)
            ));
            setMinZoom(12);
        }
        }

        private class LateralMarks extends CircleLayerDefinition {
            private static List<String> greens = Arrays.asList("c", "d", "I");
            private static List<String> reds = Arrays.asList("a", "G", "b");
            private static List<String> all = new ArrayList<>() {{
                addAll(greens);
                addAll(reds);
            }};
            {
                setId("lateraalit");
                setSource(MERIDATA);
                setSourceLayer("turvalaitteet");
                setPaint(new CirclePaint() {{
                    setCircleRadius(Interpolate
                            .exponential(1.55)
                            .zoom(
                                    new ZoomStep(6, 0.5),
                                    new ZoomStep(20, 40)
                            )
                    );
                    setCircleStrokeColor("#000080");
                    setCircleStrokeWidth(1.5);
                    setCircleOpacity(1);
                    setCircleColor(
                            new CaseExpression(
                                    // default value red
                                    "rgba(255, 0, 0, 1)",
                                    // greens
                                    PropertyEquals.anyValue("symboli", "rgba(0, 255, 0, 1)", greens))
                    );
                }});
                setFilter(new Any(Equals.anyValue("symboli", all)));
                setMinZoom(12);
            }
        }
    }
}
