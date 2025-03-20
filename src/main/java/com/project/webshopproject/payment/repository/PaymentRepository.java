package com.project.webshopproject.payment.repository;

import com.project.webshopproject.payment.entity.Payment;
import com.project.webshopproject.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentKey(String paymentKey);

    Page<Payment> findByUser(User user, Pageable pageable);
    long countByUser(User user);

}
