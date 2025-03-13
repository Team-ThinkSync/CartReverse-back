package com.project.webshopproject.cart;

import com.project.webshopproject.cart.entity.Cart;
import com.project.webshopproject.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(User user);
}
