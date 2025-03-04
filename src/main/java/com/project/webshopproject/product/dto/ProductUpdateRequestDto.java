package com.project.webshopproject.product.dto;

import com.project.webshopproject.category.entity.CategoryType;


public record ProductUpdateRequestDto(
        String productName,
        Integer productPrice,
        Integer productStock,
        String categoryName,
        CategoryType categoryType

) {

}
