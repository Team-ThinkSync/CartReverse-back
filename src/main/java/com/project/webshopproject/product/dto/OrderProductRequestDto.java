package com.project.webshopproject.product.dto;

public record OrderProductRequestDto(
         Long productId,
         String name,
         Integer price,
         Integer quantity
) {
    // 상품 결제 요청 시 담기는 상품의 정보
}
