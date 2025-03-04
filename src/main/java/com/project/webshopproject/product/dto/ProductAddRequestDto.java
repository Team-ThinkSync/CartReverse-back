package com.project.webshopproject.product.dto;

import com.project.webshopproject.category.entity.CategoryType;
public record ProductAddRequestDto(
        Long categoryId,
        String categoryName,
        CategoryType categoryType,
        String productName,
        Integer productPrice,
        Integer productStock,
        Long productImgId

) {
}



