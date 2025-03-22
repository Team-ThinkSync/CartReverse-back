package com.project.webshopproject.payment;

import com.project.webshopproject.config.PaymentConfig;
import com.project.webshopproject.payment.dto.PaymentConfirmRequestDto;
import com.project.webshopproject.payment.dto.PaymentCreateResponseDto;
import com.project.webshopproject.payment.dto.PaymentSuccessResponseDto;
import com.project.webshopproject.payment.entity.Payment;
import com.project.webshopproject.payment.entity.PaymentStatus;
import com.project.webshopproject.payment.repository.PaymentRepository;
import com.project.webshopproject.product.dto.OrderProductRequestDto;
import com.project.webshopproject.user.UserRepository;
import com.project.webshopproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentConfig paymentConfig;
    private final WebClient webClient;

    public List<PaymentCreateResponseDto> createPayment(Long userId, List<OrderProductRequestDto> orderProductRequestDtos){
        User user = userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("유저 정보가 없습니다"));

        List<PaymentCreateResponseDto> paymentCreateResponseDtos = new ArrayList<>();

        Integer totalPrice = orderProductRequestDtos.stream()
                .mapToInt(orderProductRequestDto -> orderProductRequestDto.price() * orderProductRequestDto.quantity())
                .sum(); // 총 가격  = 가격 * 수량

        for (OrderProductRequestDto orderProductRequestDto : orderProductRequestDtos) {
            String orderId = UUID.randomUUID().toString(); //  UUID를 사용해서 랜덤한 값 만들어서 orderId에 사용하기

            Payment payment = Payment.builder()
                    .user(user)
                    .orderId(orderId)
                    .orderName(orderProductRequestDto.name())
                    .totalPrice(totalPrice)
                    .paymentStatus(PaymentStatus.READY) // 결제 준비 상태
                    .build();
            paymentRepository.save(payment);

            PaymentCreateResponseDto paymentCreateResponseDto = new PaymentCreateResponseDto(
                    payment.getOrderId(),
                    payment.getOrderName(),
                    payment.getTotalPrice(),
                    payment.getStatus()
            );

            // DTO 리스트에 추가
            paymentCreateResponseDtos.add(paymentCreateResponseDto);
        }
        return paymentCreateResponseDtos;

    }


    public PaymentSuccessResponseDto confirmPayment(PaymentConfirmRequestDto paymentConfirmRequestDto){
        Payment payment = paymentRepository.findByOrderId(paymentConfirmRequestDto.orderId())
                .orElseThrow(() -> new RuntimeException("결제 정보가 없습니다."));

        //결제 금액이 일치하는지 확인
        if (!payment.getTotalPrice().equals(paymentConfirmRequestDto.totalPrice())) {
            throw new RuntimeException("결제 금액이 일치하지 않습니다.");
        }
        String basicHeader ="Basic " + Base64.getEncoder()
                .encodeToString((paymentConfig.getTestSecretApiKey() + ":").getBytes(StandardCharsets.UTF_8));

        //토스 페이먼츠 api 호출
        PaymentSuccessResponseDto responseDto = webClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION,basicHeader)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(paymentConfirmRequestDto)
                .retrieve()
                .bodyToMono(PaymentSuccessResponseDto.class)
                .block();

        payment.approvePayment(paymentConfirmRequestDto.paymentKey(),LocalDateTime.now()); // 승인
        paymentRepository.save(payment);
        return responseDto;
    }

    public void cancelPayment(String paymentKey){
        String basicHeader = "Basic " + Base64.getEncoder()
                .encodeToString((paymentConfig.getTestSecretApiKey() + ":").getBytes(StandardCharsets.UTF_8));

        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new IllegalArgumentException("해당 paymentKey가 존재하지 않습니다."));

        webClient.post()
                .uri("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel")
                .header(HttpHeaders.AUTHORIZATION, basicHeader)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{ \"cancelReason\": \"사용자 요청\" }")
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        payment.cancelPayment();
        paymentRepository.save(payment);

    }


    // 결제 내역조회 페이징
    public Page<PaymentCreateResponseDto> getUserPayments(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));

        // 페이징 처리된 결제 내역 조회
        List<PaymentCreateResponseDto> content = paymentRepository.findByUserId(userId, pageable).stream()
                .map(payment -> new PaymentCreateResponseDto(
                        payment.getOrderId(),
                        payment.getOrderName(),
                        payment.getTotalPrice(),
                        payment.getStatus()
                ))
                .toList();

        // 전체 결제 내역 개수 조회
        long total = paymentRepository.countByUserId(userId);

        return new PageImpl<>(content, pageable, total);
    }
}
