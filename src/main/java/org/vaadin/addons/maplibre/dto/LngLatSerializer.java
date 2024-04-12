package org.vaadin.addons.maplibre.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class LngLatSerializer extends JsonSerializer<LngLat> {

    @Override
    public void serialize(LngLat value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeArray(new double[]{value.lng(), value.lat()},0,2);
    }

}
