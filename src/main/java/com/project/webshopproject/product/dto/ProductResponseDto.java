package com.project.webshopproject.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductResponseDto {
    private Long productId;
    private Long categoryId;
    private String categoryType;
    private String categoryName;
    private String productName;
    private Integer productPrice;
    private Integer productStock;
    private String productImage;

    @QueryProjection
    public ProductResponseDto(Long productId, Long categoryId, String categoryType, String categoryName,
                              String name, Integer price, Integer stock, String productImage ) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.productName = name;
        this.productPrice = price;
        this.productStock = stock;
        this.productImage = productImage;
    }
}
