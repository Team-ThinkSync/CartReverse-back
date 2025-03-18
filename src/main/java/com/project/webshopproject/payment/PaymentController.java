package com.project.webshopproject.payment;

import com.project.webshopproject.payment.dto.PaymentConfirmRequestDto;
import com.project.webshopproject.payment.dto.PaymentCreateResponseDto;
import com.project.webshopproject.product.dto.OrderProductRequestDto;
import com.project.webshopproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/payment/request") // 결제 요청
    public List<PaymentCreateResponseDto> createPaymentRequest(@AuthenticationPrincipal final UserDetailsImpl userDetails,
                                                               @RequestBody List<OrderProductRequestDto> orderProductRequestDto){
        return paymentService.createPayment(userDetails.getUser().getUserId(),orderProductRequestDto);
    }
    @PostMapping("/payment/confirm") // 결제 승인
    public void confirmPayment(@RequestBody PaymentConfirmRequestDto paymentConfirmRequestDto){
        paymentService.confirmPayment(paymentConfirmRequestDto);
    }
    @PostMapping("/payment/cancel")
    public void cancelPayment(@RequestBody String paymentKey) {
        paymentService.cancelPayment(paymentKey);
    }
}
