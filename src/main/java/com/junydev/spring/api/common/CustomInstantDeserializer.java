package com.junydev.spring.api.common;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CustomInstantDeserializer extends JsonDeserializer<Instant> {

    public static final CustomInstantDeserializer INSTANCE = new CustomInstantDeserializer();

    private final String dateFormat;
    private final String timeZone;

    public CustomInstantDeserializer() {
        this.dateFormat = "yyyy-MM-dd'T'HH:mm:ss SSS'Z'";
        this.timeZone = "UTC";
    }

    public CustomInstantDeserializer(String dateFormat, String timeZone) {
        this.dateFormat = dateFormat;
        this.timeZone = timeZone;
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String value = p.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.dateFormat).withZone(ZoneId.of(this.timeZone));
        return Instant.from(dateTimeFormatter.parse(value));
    }
}
