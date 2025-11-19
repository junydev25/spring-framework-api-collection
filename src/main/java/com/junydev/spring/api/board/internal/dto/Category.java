package com.junydev.spring.api.board.internal.dto;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum Category {
    GAME,
    SPORTS,
    IT;

    @JsonCreator
    public static Category from(String value) {
        return value == null ? null : Category.valueOf(value.toUpperCase());
    }
}
