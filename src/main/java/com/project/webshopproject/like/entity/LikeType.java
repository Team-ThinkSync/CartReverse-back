package com.project.webshopproject.like.entity;

import lombok.Getter;

@Getter
public enum LikeType {
    PRODUCT("상품"),
    REVIEW("리뷰");

    private final String description;

    LikeType(String description) {
        this.description = description;
    }
}
