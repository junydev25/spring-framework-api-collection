package com.junydev.spring.api.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junydev.spring.api.board.internal.dto.Category;

import java.io.IOException;

public class EnumSerializer extends JsonSerializer<Category> {

    public EnumSerializer() {
    }

    @Override
    public void serialize(Category value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(value.name().toLowerCase());
        }
    }
}
