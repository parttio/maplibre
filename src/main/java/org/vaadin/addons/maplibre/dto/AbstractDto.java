package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

// TODO figure out if the KebabCaseStrategy is only needed
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractDto {
    static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Object[] arr(Object... objects) {
        return objects;
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
}
