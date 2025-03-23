package com.project.webshopproject.payment.dto;

public record PaymentConfirmRequestDto(
        String orderId,
        Integer totalPrice,
        String paymentKey
) {
    // 결제 요청 이후 paymentKey가 반환되어서 돌아올때
}
