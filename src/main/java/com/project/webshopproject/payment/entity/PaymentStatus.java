package com.project.webshopproject.payment.entity;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    READY("결제요청"),      // 결제 요청됨 (초기 상태)
    DONE("결제완료"),       // 결제 완료됨
    CANCELED("결제취소");   // 결제 취소됨

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
