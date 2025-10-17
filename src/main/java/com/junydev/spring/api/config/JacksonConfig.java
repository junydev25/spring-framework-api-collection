package com.junydev.spring.api.config;

import com.junydev.spring.api.common.CustomInstantDeserializer;
import com.junydev.spring.api.common.CustomInstantSerializer;
import com.junydev.spring.api.common.EnumDeserializer;
import com.junydev.spring.api.common.EnumSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer(@Value("${spring.jackson.date-format}") String dateTimeFormat,
                                                                   @Value("${spring.jackson.time-zone}") String timeZone) {
        return  builder ->
            builder.serializerByType(Instant.class, new CustomInstantSerializer(dateTimeFormat, timeZone))
                    .deserializerByType(Instant.class, new CustomInstantDeserializer(dateTimeFormat, timeZone))
                    .serializerByType(Enum.class, new EnumSerializer())
                    .deserializerByType(Enum.class, new EnumDeserializer());
    }
}