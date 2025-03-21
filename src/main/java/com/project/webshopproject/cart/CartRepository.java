package com.project.webshopproject.cart;

import com.project.webshopproject.cart.entity.Cart;
import com.project.webshopproject.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Page<Cart> findByUser(User user, Pageable pageable);
}
