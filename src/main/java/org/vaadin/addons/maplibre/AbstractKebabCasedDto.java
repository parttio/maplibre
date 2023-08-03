package org.vaadin.addons.maplibre;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class AbstractKebabCasedDto {
    static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setPropertyNamingStrategy(new PropertyNamingStrategies.KebabCaseStrategy());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
