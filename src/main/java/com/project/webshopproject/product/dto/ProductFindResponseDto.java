package com.project.webshopproject.product.dto;

import com.project.webshopproject.review.dto.ReviewResponseDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductFindResponseDto {
    private String productName;
    private String productImg;
    private Integer productPrice;
    private Integer productStock;
    private String categoryType;
    private String categoryName;
    private Long likeCount;

    @QueryProjection
    public ProductFindResponseDto(String productName, Integer productPrice, Integer productStock,
                                  String productImg, String categoryType, String categoryName,Long likeCount) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.productImg = productImg;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
        this.likeCount = likeCount;
    }
}
