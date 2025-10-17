package com.junydev.spring.api.board;

import com.junydev.spring.api.board.internal.dto.Category;
import com.junydev.spring.api.board.internal.dto.StatType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BoardConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Category>() {
            @Override
            public Category convert(String source) {
                return Category.valueOf(source.toUpperCase());
            }
        });
        registry.addConverter(new Converter<String, StatType>() {
            @Override
            public StatType convert(String source) {
                return StatType.valueOf(source.toUpperCase());
            }
        });
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
