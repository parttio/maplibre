package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.util.HashMap;
import java.util.Map;

public class AbstractKebabCasedDto {
    public static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setPropertyNamingStrategy(new PropertyNamingStrategies.KebabCaseStrategy());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private Map<String, Object> supplemental = new HashMap<>();

    /**
     * @return Map of supplemental properties that are not part of the DTO class. These are serialized to the JSON
     * object.
     */
    @JsonAnyGetter
    public Map<String, Object> getSupplemental() {
        if(supplemental == null) {
            supplemental = new HashMap<>();
        }
        return supplemental;
    }

    /**
     * Set supplemental properties from the given JSON string. Can be used to configure some properties that are not
     * (yet) published as a proper API in the DTO class.
     *
     * @param supplementalJson JSON string to set the supplemental properties from
     */
    public void setSupplementalJson(String supplementalJson) {
        try {
            JsonNode jsonNode = mapper.readTree(supplementalJson);
            jsonNode.fields().forEachRemaining(entry -> {
                supplemental.put(entry.getKey(), entry.getValue());
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // JSON representation to be easily used in JS calls
    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{'value': 'JSON ERROR'}";
        }
    }

    protected static String camelToKebabCase(String str) {
        return str.replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase();
    }
}
