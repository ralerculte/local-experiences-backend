package com.cultegroup.findguide.data.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class IdSerializer extends JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            String id = ((EntityId) value).getId() + "";
            gen.writeString(id);
        } catch (Exception e) {
            throw new RuntimeException("Некорректное использование @JsonSerialize");
        }
    }
}
