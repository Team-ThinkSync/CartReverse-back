package com.project.webshopproject.product.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductEditRequestDto {
    private String productName;
    private List<String> productImg;
    private Integer productPrice;
    private Integer productStock;

}
