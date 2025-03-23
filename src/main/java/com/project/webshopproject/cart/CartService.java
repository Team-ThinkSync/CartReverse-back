package com.project.webshopproject.cart;

import com.project.webshopproject.cart.dto.CartAddRequestDto;
import com.project.webshopproject.cart.dto.CartResponseDto;
import com.project.webshopproject.cart.entity.Cart;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.repository.ProductRepository;
import com.project.webshopproject.user.UserService;
import com.project.webshopproject.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "Cart Service")
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public void addCart(CartAddRequestDto requestDto, String email) {
        User user = userService.findByEmail(email);
        Product product = productRepository.findById(requestDto.productId()).orElseThrow(() -> {
            log.error("제품을 찾지 못함 | request : {}", requestDto.productId());
            return new IllegalArgumentException("제품을 찾지 못했습니다.");
        });;
        Cart cart = new Cart(user, product, requestDto.quantity());

        cartRepository.save(cart);
    }

    // 장바구니 전체 조회
    public Page<CartResponseDto> getAllCartItem(String email, Pageable pageable) {
        User user = userService.findByEmail(email);
        Page<Cart> cartPage = cartRepository.findByUser_UserId(user.getUserId(), pageable);

        List<CartResponseDto> cartDtoList = cartPage.getContent().stream()
                .map(cart -> new CartResponseDto(
                        cart.getProduct().getName(),
                        cart.getQuantity()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(cartDtoList, pageable, cartPage.getTotalElements());
    }

    // 장바구니 항목 삭제
    public void deleteCart(Long cartId, String email) {
        User user = userService.findByEmail(email);
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("장바구니 항목을 찾을 수 없습니다."));

        if (!cart.getUser().equals(user)) {
            throw new RuntimeException("해당 사용자만 삭제할 수 있습니다.");
        }

        cartRepository.delete(cart);
    }

}
