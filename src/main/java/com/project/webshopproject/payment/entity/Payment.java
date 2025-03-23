package com.project.webshopproject.payment.entity;

import com.project.webshopproject.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name="order_id",nullable = false, unique = true)
    private String orderId;  // 주문 ID (유니크 값)

    @Column(name = "payment_key")
    private String paymentKey;  // 결제 승인 후 받은 PaymentKey

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;  // 결제 상태 (READY, DONE, CANCELED)

    @Column(name="approved_at")
    private LocalDateTime approvedAt;  // 결제 승인 시간

    @Column(name="created_at")
    private LocalDateTime createdAt;  // 결제 요청 시간

    @Column(name = "order_name")
    private String orderName; // 결제 목록

    @Column(name = "total_price")
    private Integer totalPrice; // 총 금액

    @Builder
    public Payment(User user,String orderId, Integer totalPrice, String orderName, PaymentStatus paymentStatus){
        this.user = user;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderName = orderName;
        this.status = paymentStatus;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void approvePayment(String paymentKey, LocalDateTime approvedAt) {
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
        this.status = PaymentStatus.DONE;
    }

    public void cancelPayment() {
        this.status = PaymentStatus.CANCELED;
    }


}
