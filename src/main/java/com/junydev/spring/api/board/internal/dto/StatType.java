package com.junydev.spring.api.board.internal.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatType {
    LIKE,
    VIEW,
    COMMENT;

    @JsonCreator
    public static Category from(String value) {
        return value == null ? null : Category.valueOf(value.toUpperCase());
    }
}
