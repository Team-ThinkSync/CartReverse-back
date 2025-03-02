package com.project.webshopproject.product;

import com.project.webshopproject.product.dto.ProductAddRequestDto;
import com.project.webshopproject.product.dto.ProductFindResponseDto;
import com.project.webshopproject.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    //단일 상품 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductFindResponseDto> getProductById(@PathVariable("productId") Long productId) {
        ProductFindResponseDto product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }
    //상품 추가
    @PostMapping("/product")
    public ResponseEntity<String> addProduct(@RequestPart("dto") ProductAddRequestDto productAddRequestDto,
                                             @RequestPart("image") final List<MultipartFile> images){
        productService.addProduct(productAddRequestDto,images);
        return ResponseEntity.ok("상품추가에 성공하였습니다");
    }

//    @PatchMapping("/products/{productId}") // 상품 수정
//    public ResponseEntity<ItemEditDto> editItem(@PathVariable Long itemId,
//                                                @RequestPart("dto") ItemEditDto itemEditDto,
//                                                @RequestPart(value = "image") final List<MultipartFile> images){
//        ItemEditDto updatedItem = itemService.editItem(itemId,itemEditDto, image);
//        return ResponseEntity.ok(updatedItem);
//    }
    @DeleteMapping("product/{productId}") // 상품 삭제
    public ResponseEntity<String> deleteItem(@PathVariable("productId") Long productId){
        productService.deleteProduct(productId);
        return ResponseEntity.ok("상품삭제에 성공하였습니다");
    }

}
