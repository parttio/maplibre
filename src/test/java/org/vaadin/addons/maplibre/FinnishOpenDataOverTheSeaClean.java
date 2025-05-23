package org.vaadin.addons.maplibre;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.addons.maplibre.dto.CircleLayerDefinition;
import org.vaadin.addons.maplibre.dto.CirclePaint;
import org.vaadin.addons.maplibre.dto.FillLayerDefinition;
import org.vaadin.addons.maplibre.dto.LineLayerDefinition;
import in.virit.color.RgbColor;
import org.vaadin.addons.maplibre.dto.RootDefinition;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route
public class FinnishOpenDataOverTheSeaClean extends VerticalLayout {
    static final String MERIDATA = "meridata";

    private Marker marker;

    public FinnishOpenDataOverTheSeaClean() throws IOException, URISyntaxException {
        add(new H1("Finnish Open Data over the sea"));

        MapLibre map = new FinnishTerrainMapWithSomeNauticalChartDataMap();
        addAndExpand(map);

    }

    class FinnishTerrainMapWithSomeNauticalChartDataMap extends MapLibre {

        static class EmptyRootDefinition extends RootDefinition {
            public EmptyRootDefinition() {
                setName("empty");
                //setSupplementalJson(BASEJSON);
            }
        }

        public FinnishTerrainMapWithSomeNauticalChartDataMap() throws IOException, URISyntaxException {
            super(new URI("https://api.maptiler.com/maps/streets/style.json?key=G5n7stvZjomhyaVYP0qU"));
            addSprite("meri", "https://virit.in/maastokartta/merisprite");

            // This raw Vector Tile sources contains:
            // - Depth contours. areas and soundings by Traficom
            // - Fairways, beacons, etc. by Väylävirasto
            // Packaged together with GDAL & Tippecanoe
            // The service URL is open, but runs on a tiny server -> don't use it for anything else but testing
            // TODO re-package the data so that boyes, etc. are not dropped on smaller zoom levels
            addSource(MERIDATA, new VectorMapSource("https://fvtns.dokku1.parttio.org/services/meridata2/"));

            // Stones, rocks, etc. by MML
            //addSource("mml", new VectorMapSource("https://avoin-karttakuva.maanmittauslaitos.fi/vectortiles/tilejson/taustakartta/1.0.0/taustakartta/default/v20/WGS84_Pseudo-Mercator/tilejson.json?api-key=8b7770cf-8589-4ebe-acbb-ab8876b684ce"));
            //addSource("mml", new VectorMapSource("http://localhost:8000/services/mtk"));

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
                    setTextRotationAlignment(TextRotationAlignment.map);
                    setTextTransform(TextTransform.uppercase);
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

            if(false)
            addSourceLayer(new CircleLayerDefinition() {{
                setId("muut-merkit");
                setSource(MERIDATA);
                setSourceLayer("turvalaitteet");
                setMinZoom(12);
                // Filter out some symbols that are handled by some other layer
                var alreadyHandledSymbols = new ArrayList<>(LateralMarks.all);
                alreadyHandledSymbols.addAll(allCardinalMarks);
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

            addSourceLayer(new WestMarks());
            addSourceLayer(new EastMarks());
            addSourceLayer(new NorthMarks());
            addSourceLayer(new SouthMarks());
            addSourceLayer(new LateralMarks());

            // These come from "mml" source (maastotietokanta)
            addSourceLayer(new RockBelowSurface());
            addSourceLayer(new RockSurface());
            addSourceLayer(new RockAboveSurface());

//            flyTo(22.2462, 60.1755, 8.0);
            setZoomLevel(14.0);
            //setCenter(22.95, 59.820);
            setCenter(22.2462, 60.1755);

        }

        static final List<String> eastMarks = Arrays.asList("k", "l", "Q");
        static final List<String> westMarks = Arrays.asList("j", "O", "i");
        static final List<String> northMarks = Arrays.asList("K", "f", "9");
        static final List<String> southMarks = Arrays.asList("A", "h", "M", "g", "N");
        static final List<String> allCardinalMarks = new ArrayList<>(){{
            addAll(eastMarks);
            addAll(westMarks);
            addAll(southMarks);
            addAll(northMarks);
        }};

        private abstract class CardinalMark extends SymbolLayerDefinition {{
            setMinZoom(12);
            setSource(MERIDATA);
            setSourceLayer("turvalaitteet");
            setLayout(new SymbolLayout() {{
                setIconSize(0.75);
                setIconImage(symbolName());
                setIconAllowOverlap(true);
            }});
        }

            abstract String symbolName();
        }

        private class EastMarks extends CardinalMark {
            {
                setId("kardinaalit-itä");
                setFilter(new Any(
                        Equals.anyValue("symboli", eastMarks)
                ));
            }

            @Override
            String symbolName() {
                return "meri:beacon_byb";
            }
        }

        private class WestMarks extends CardinalMark {
            {
                setId("kardinaalit-länsi");
                setFilter(new Any(
                        Equals.anyValue("symboli", westMarks)
                ));
            }

            @Override
            String symbolName() {
                return "meri:beacon_yby";
            }
        }

        private class NorthMarks extends CardinalMark {
            {
                setId("kardinaalit-pohjoinen");
                setFilter(new Any(
                        Equals.anyValue("symboli", northMarks)
                ));
            }

            @Override
            String symbolName() {
                return "meri:beacon_by";
            }
        }

        private class SouthMarks extends CardinalMark {
            {
                setId("kardinaalit-etelä");
                setFilter(new Any(
                        Equals.anyValue("symboli", southMarks)
                ));
            }

            @Override
            String symbolName() {
                return "meri:beacon_yb";
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

        private abstract class Rock extends SymbolLayerDefinition {
            {
                setSource(MERIDATA);
                setSourceLayer("vesikivi");
                setLayout(new SymbolLayout() {{
//                    setIconSize(0.125);
//                    setIconImage(symbolName());
//                    setIconRotate(iconRotate());
//                    setIconAllowOverlap(true);
                }

                });
                setFilter(new Equals("kohdeluokka", kohdeluokka()));
                setMinZoom(9);
                setMaxZoom(20);
            }

            int iconRotate() {
                return 0;
            }

            abstract String kohdeluokka();

            abstract String symbolName();
        }

                    /*
                {
      "id": "maasto_poi",
      "type": "circle",
      "source": "taustakartta",
      "source-layer": "maasto_piste",
      "filter": [
        "match",
        ["get", "kohdeluokka"],
        [34600, 72310, 38511, ],
        true,
        false
      ],
      "paint": {"circle-color": "#8d7469", "circle-radius": 3}
    }
             */

        private class RockBelowSurface extends Rock {
            {
            }

            @Override
            String kohdeluokka() {
                return "38511";
            }

            @Override
            String symbolName() {
                return "meri:rock-normal";
            }
        }

        private class RockSurface extends Rock {

            @Override
            String kohdeluokka() {
                return "38512";
            }

            @Override
            String symbolName() {
                return "meri:rock-wl";
            }
        }

        private class RockAboveSurface extends Rock {

            @Override
            String kohdeluokka() {
                return "38513";
            }

            @Override
            String symbolName() {
                return "meri:rock-normal";
            }

            @Override
            int iconRotate() {
                return 180;
            }
        }

    }

}
