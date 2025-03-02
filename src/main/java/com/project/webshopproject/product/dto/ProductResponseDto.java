package com.project.webshopproject.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponseDto {
    private Long productId;
    private Long categoryId;
    private String categoryType;
    private String categoryName;
    private String productName;
    private String productImg;
    private Integer productPrice;
    private Integer productStock;

    @QueryProjection
    public ProductResponseDto(Long productId, Long categoryId, String categoryType, String categoryName,
                              String name, String mainImage, Integer price, Integer stock ) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.productName = name;
        this.productImg = mainImage;
        this.productPrice = price;
        this.productStock = stock;
    }
}
