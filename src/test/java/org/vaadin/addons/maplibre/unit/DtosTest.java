package org.vaadin.addons.maplibre.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.maplibre.dto.Bounds;
import org.vaadin.addons.maplibre.dto.CircleLayerDefinition;
import org.vaadin.addons.maplibre.dto.CirclePaint;
import org.vaadin.addons.maplibre.dto.LayerDefinition;
import org.vaadin.addons.maplibre.dto.SymbolLayerDefinition;
import org.vaadin.addons.maplibre.dto.VectorMapSource;

public class DtosTest {

    @Test
    public void testBounds() throws JsonProcessingException {
        var bounds = new Bounds(1, 2, 3, 4);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(bounds);
        Assertions.assertEquals(1, jsonNode.get(0).asDouble());
        Assertions.assertEquals(2, jsonNode.get(1).asDouble());
        Assertions.assertEquals(3, jsonNode.get(2).asDouble());
        Assertions.assertEquals(4, jsonNode.get(3).asDouble());

        String s = objectMapper.writeValueAsString(bounds);
        Assertions.assertEquals("[1.0,2.0,3.0,4.0]", s);

    }

    @Test
    public void testSources() throws JsonProcessingException {
        VectorMapSource vectorMapSource = new VectorMapSource("https://example.com/foo/bar");

        var mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.valueToTree(vectorMapSource);
        int size = jsonNode.size();
        Assertions.assertEquals(2, size);

    }

    @Test
    public void testLayerDto() {
        LayerDefinition layerDto = new LayerDefinition("layer-id", LayerDefinition.LayerType.line);
        layerDto.setSourceLayer("foobar");
        String json = layerDto.toString();
        Assertions.assertTrue(json.contains("source-layer"));
        Assertions.assertTrue(json.contains("type"));
        Assertions.assertTrue(json.contains("layer-id"));
    }

    @Test
    public void testMinMaxZoom() {
        SymbolLayerDefinition symbolLayerDefinition = new SymbolLayerDefinition() {{
            setMinZoom(12);
            setMaxZoom(15);
        }};

        String json = symbolLayerDefinition.toString();
        Assertions.assertTrue(json.contains("minzoom"));
        Assertions.assertTrue(json.contains("maxzoom"));
    }

    @Test
    public void testLayerPaint() {
        CirclePaint circlePaint = new CirclePaint();
        circlePaint.setCircleStrokeWidth(2);
        String json = circlePaint.toString();
        Assertions.assertTrue(json.contains("circle-stroke-width"));
        System.out.println(json);


        var cld = new CircleLayerDefinition() {{
            setId("muut-merkit");
            setSource("meridata");
            setSourceLayer("turvalaitteet");
            setMinZoom(12);
            setPaint(new CirclePaint(){{
                    /*
                    setCircleRadius(new CircleRadius(){{
                        setInterpolate(new Interpolate(){{
                            setExponential(1.55);
                            setZoom();
                            setStops(new int[][]{{6, 0.75}, {20, 30}});
                        }});
                    }});
                     */
                setCircleStrokeColor("#00FFFF");
                setCircleStrokeWidth(2);
                setCircleOpacity(1);

                setSupplementalJson("""
                {
                        "circle-radius": [
                          "interpolate",
                          ["exponential", 1.55],
                          ["zoom"],
                          6,
                          0.75,
                          20,
                          30
                        ],
                        "circle-color": [
                            "case",
                            ["==", ["get", "symboli"], "w"],
                            "rgba(255, 255, 255, 1)",
                            ["==", ["get", "symboli"], "2"],
                            "rgba(255, 255, 255, 1)",
                            "rgba(0, 200, 200, 1)"
                        ]
                    },
                    "filter": [
                      "all",
                      ["!=", "symboli", "j"],
                      ["!=", "symboli", "l"],
                      ["!=", "symboli", "Q"],
                      ["!=", "symboli", "O"],
                      ["!=", "symboli", "h"],
                      ["!=", "symboli", "M"],
                      ["!=", "symboli", "K"],
                      ["!=", "symboli", "i"],
                      ["!=", "symboli", "k"],
                      ["!=", "symboli", "f"],
                      ["!=", "symboli", "l"],
                      ["!=", "symboli", "a"],
                      ["!=", "symboli", "b"],
                      ["!=", "symboli", "c"],
                      ["!=", "symboli", "d"]
                    ]
                }
                """);

                    /*
                    setCircleColor(new CircleColor(){{
                        setCase(new Case(){{
                            setEquals(new Equals(){{
                                setGet("symboli");
                                setValue("w");
                            }});
                            setValue("rgba(255, 255, 255, 1)");
                            setEquals(new Equals(){{
                                setGet("symboli");
                                setValue("2");
                            }});
                            setValue("rgba(255, 255, 255, 1)");
                            setValue("rgba(0, 200, 200, 1)");
                        }});
                     */
            }});

        }};

        System.out.println(prettyPrint(cld));

    }

    static String prettyPrint(Object json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json.toString());
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
