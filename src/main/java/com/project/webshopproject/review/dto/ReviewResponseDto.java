package com.project.webshopproject.review.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponseDto(
        Long reviewId,
        Long userId,
        Long productId,
        String title,
        String content,
        int rate,
        LocalDateTime createdAt,
        List<String> imageUrls,
        Long likeCount
) {}