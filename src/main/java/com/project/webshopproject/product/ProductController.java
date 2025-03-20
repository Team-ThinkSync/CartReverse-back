package com.project.webshopproject.product;

import com.project.webshopproject.product.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    //모든 상품 조회
    @GetMapping("/product")
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }
    //단일 상품 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductFindResponseDto> getProductById(@PathVariable("productId") Long productId) {
        ProductFindResponseDto product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }
    //카테고리 별 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductByCategoryResponseDto>> getProductByCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(productService.getProductByCategory(categoryId, page, size));
    }
    //상품 추가
    @PostMapping("/product")
    public ResponseEntity<String> addProduct(@RequestPart("dto") ProductAddRequestDto productAddRequestDto,
                                             @RequestPart("image") final List<MultipartFile> images){
        productService.addProduct(productAddRequestDto,images);
        return ResponseEntity.ok("상품추가에 성공하였습니다");
    }

    // 상품 수정
    @PatchMapping("/product/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Long productId,
                                                                 @RequestPart("dto") ProductUpdateRequestDto productUpdateRequestDto,
                                                                 @RequestPart(value = "image") final List<MultipartFile> images){
        productService.updateProduct(productId, productUpdateRequestDto, images);
        return ResponseEntity.ok("상품 수정에 성공하였습니다");
    }
    // 상품 삭제
    @DeleteMapping("product/{productId}")
    public ResponseEntity<String> deleteItem(@PathVariable("productId") Long productId){
        productService.deleteProduct(productId);
        return ResponseEntity.ok("상품삭제에 성공하였습니다");
    }

}
