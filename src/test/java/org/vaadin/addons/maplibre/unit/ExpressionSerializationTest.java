package org.vaadin.addons.maplibre.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.maplibre.dto.expressions.All;
import org.vaadin.addons.maplibre.dto.expressions.CaseExpression;
import org.vaadin.addons.maplibre.dto.expressions.Equals;
import org.vaadin.addons.maplibre.dto.expressions.Expression;
import org.vaadin.addons.maplibre.dto.expressions.Interpolate;
import org.vaadin.addons.maplibre.dto.expressions.NotEquals;
import org.vaadin.addons.maplibre.dto.expressions.PropertyEquals;
import org.vaadin.addons.maplibre.dto.expressions.ZoomStep;

public class ExpressionSerializationTest {

    ObjectMapper mapper = new ObjectMapper();

    private static final String KUMMELI_A = "w";
    private static final String KUMMELI_B = "1";

    @Test
    public void testCase() {
        /*
        "circle-color":
        [
            "case",
            ["==", ["get", "symboli"], "w"],
            "rgba(255, 255, 255, 1)",
            ["==", ["get", "symboli"], "2"],
            "rgba(255, 255, 255, 1)",
            "rgba(0, 200, 200, 1)"
        ]

         */

        var caseExpression = new CaseExpression();
        caseExpression.add(PropertyEquals.anyValue("symboli", "rgba(255, 255, 255, 1)", KUMMELI_A, KUMMELI_B));
        caseExpression.setDefaultValue("rgba(0, 200, 200, 1)");
        System.out.println(prettyPrint(caseExpression));

    }


    @Test
    public void testAllPredicate() {
        /*
          "filter": [
              "all",
              ["!=", "symboli", "j"],
              ["==", "toinen", "l"]
         ]
         */

        var expression = new All(
                new NotEquals("symboli", "j"),
                new Equals("symboli", "l")
        );

        JsonNode jsonNode = mapper.valueToTree(expression);
        Assertions.assertEquals(3, jsonNode.size());

        expression = new All(
                NotEquals.anyValue("symboli", "a", "b", "c")
        );
        jsonNode = mapper.valueToTree(expression);

        Assertions.assertEquals(4, jsonNode.size());

        expression = new All(
                Equals.anyValue("symboli", "a", "b", "c")
        );
        jsonNode = mapper.valueToTree(expression);
        Assertions.assertEquals(4, jsonNode.size());


    }

    @Test
    void testInterpolate() {
        /*
           "circle-radius": [
              "interpolate",
              ["exponential", 1.55],
              ["zoom"],
              6,
              0.75,
              20,
              30
            ]

         */

        var expr = Interpolate
                .exponential(1.55)
                .zoom(
                    new ZoomStep(6, 0.75),
                    new ZoomStep(20, 30)
                );

        System.out.println(prettyPrint(expr));

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
