package com.project.webshopproject.category.entity;

import lombok.Getter;
@Getter
public enum CategoryType {
    MALE("남자"),
    FEMALE("여자"),
    UNISEX("공용");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }
}
