package com.project.webshopproject.cart;

import com.project.webshopproject.cart.dto.CartAddRequestDto;
import com.project.webshopproject.cart.dto.CartResponseDto;
import com.project.webshopproject.common.RestApiResponseDto;
import com.project.webshopproject.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;

    @PostMapping("/carts")
    public ResponseEntity<RestApiResponseDto<String>> addCart(
            @Valid @RequestBody final CartAddRequestDto requestDto,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        try {
            cartService.addCart(requestDto, userDetails.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RestApiResponseDto.of("장바구니에 추가되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RestApiResponseDto.of(e.getMessage()));
        }
    }

    // 장바구니 조회
    @GetMapping("/carts")
    public ResponseEntity<RestApiResponseDto<List<CartResponseDto>>> getCart(
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        try {
            List<CartResponseDto> cartItems = cartService.getAllCartItem(userDetails.getEmail());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(RestApiResponseDto.of("장바구니 전체조회했습니다.", cartItems));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RestApiResponseDto.of(e.getMessage()));
        }
    }

    // 장바구니 항목 삭제
    @DeleteMapping("/carts/{cartId}")
    public ResponseEntity<RestApiResponseDto<String>> deleteCart(
            @PathVariable Long cartId,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        try {
            cartService.deleteCart(cartId, userDetails.getEmail());
            return ResponseEntity.ok(RestApiResponseDto.of("장바구니 항목이 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RestApiResponseDto.of(e.getMessage()));
        }
    }
}
