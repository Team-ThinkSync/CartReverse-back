package com.project.webshopproject.review.dto;

public record ReviewRequestDto(
        String title,
        String content,
        int rate
) {}