package com.project.webshopproject.payment;

import com.project.webshopproject.payment.dto.PaymentConfirmRequestDto;
import com.project.webshopproject.payment.dto.PaymentCreateResponseDto;
import com.project.webshopproject.product.dto.OrderProductRequestDto;
import com.project.webshopproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/payment")
    public ResponseEntity<Page<PaymentCreateResponseDto>> getUserPayments(
            @AuthenticationPrincipal final UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(paymentService.getUserPayments(userDetails.getUser().getUserId(), pageable));
    }
}
