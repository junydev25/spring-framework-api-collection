package com.junydev.spring.api.common;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.junydev.spring.api.board.internal.dto.Category;

import java.io.IOException;

public class EnumDeserializer extends JsonDeserializer<Category> {

    public EnumDeserializer() {
    }

    @Override
    public Category deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String value = p.getText();
        if (value == null) return null;
        return Category.valueOf(value.toUpperCase());
    }
}
