package com.project.webshopproject.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewRequestDto(
        String title,
        String content,
        @Min(0) @Max(5) Integer rate // 0 ~ 5 사이의 값만 허용
) {}