package com.project.webshopproject.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CartAddRequestDto (
        @NotBlank(message = "itemId는 비어 있을 수 없습니다.")
        Long productId,

        @Min(value = 1, message = "quantity는 1 이상이어야 합니다.")
        int quantity
){

}
