package com.project.webshopproject.review.dto;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long reviewId,
        Long userId,
        Long productId,
        String title,
        String content,
        int rate,
        LocalDateTime createdAt
) {}