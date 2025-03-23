package com.project.webshopproject.like.dto;

import com.project.webshopproject.like.entity.LikeType;
import jakarta.validation.constraints.NotBlank;

public record LikeRequestDto(
        Long targetId, // 상품 or 리뷰 ID
        LikeType likeType// 좋아요 타입 (PRODUCT or REVIEW)
) {
}
