package com.project.webshopproject.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductByCategoryResponseDto {
    private String productName;
    private Integer productPrice;
    private Integer productStock;
    private String productImage;

    @QueryProjection
    public ProductByCategoryResponseDto(String productName, Integer productPrice, Integer productStock,String productImage){
        this.productName= productName;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.productImage = productImage;
    }

}

