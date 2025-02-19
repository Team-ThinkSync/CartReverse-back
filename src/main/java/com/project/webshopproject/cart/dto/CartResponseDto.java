package com.project.webshopproject.cart.dto;

/**
 * @param productName final 선언
 * @param quantity final 선언
 */
public record CartResponseDto(
        String productName,
        int quantity
) {

}
