package com.junydev.spring.api.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class CustomInstantSerializer extends JsonSerializer<Instant> {

    public static final CustomInstantSerializer INSTANCE = new CustomInstantSerializer();

    private final String dateFormat;
    private final String timeZone;

    public CustomInstantSerializer() {
        this.dateFormat = "yyyy-MM-dd'T'HH:mm:ss SSS'Z'";
        this.timeZone = "UTC";
    }

    public CustomInstantSerializer(String dateFormat, String timeZone) {
        this.dateFormat = dateFormat;
        this.timeZone = timeZone;
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            String formatted = DateTimeFormatter.ofPattern(this.dateFormat).withZone(ZoneId.of(this.timeZone)).format(value);
            gen.writeString(formatted);
        }
    }
}
