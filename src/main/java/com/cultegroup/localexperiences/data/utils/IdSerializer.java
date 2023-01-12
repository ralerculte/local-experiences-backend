package com.cultegroup.localexperiences.data.utils;

import com.cultegroup.localexperiences.data.model.Experience;
import com.cultegroup.localexperiences.data.model.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class IdSerializer extends JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value instanceof User) {
            gen.writeString(String.valueOf(((User) value).getId()));
        } else if (value instanceof Experience) {
            gen.writeString(String.valueOf(((Experience) value).getId()));
        } else {
            throw new IOException("Некорректное использование @JsonSerialize");
        }
    }
}
