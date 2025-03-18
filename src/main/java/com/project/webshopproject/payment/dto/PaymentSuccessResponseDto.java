package com.project.webshopproject.payment.dto;

import java.time.LocalDateTime;

public class PaymentSuccessResponseDto {
    private String orderId;
    private String orderName;
    private String paymentKey;
    private String status;
    private LocalDateTime approvedAt;
}
