package com.project.webshopproject.payment.dto;

import com.project.webshopproject.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCreateResponseDto {
       private String orderId;
       private String orderName;
       private Integer totalPrice;
       private PaymentStatus paymentStatus;
       // 사용자가 결제 요청한 상품에 대한 결제요청
}
