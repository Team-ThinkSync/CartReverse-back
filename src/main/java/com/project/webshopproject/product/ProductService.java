package com.project.webshopproject.product;

import com.project.webshopproject.category.entity.ProductCategory;
import com.project.webshopproject.category.repository.ProductCategoryRepository;
import com.project.webshopproject.product.dto.*;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.entity.ProductImage;
import com.project.webshopproject.product.repository.ProductImageRepository;
import com.project.webshopproject.product.repository.ProductQueryRepository;
import com.project.webshopproject.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Value("${file.upload-dir}")
    private String uploadDir; // 이미지 파일 저장 되는 경로

    // 전체 상품 조회
    public Page<ProductResponseDto> getAllProducts(int page, int size){
       // productQueryRepository.findAllProducts();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "productId"));
        return productQueryRepository.findAllProducts(pageable);
    }

    //카테고리별 조회 api 추가
    public Page<ProductByCategoryResponseDto> getProductByCategory(Long categoryId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "name"));
        return productQueryRepository.getProductByCategory(categoryId, pageable);
    }

     // 세부 상품 조회
     public ProductFindResponseDto getProductById(Long productId) {
         return productQueryRepository.findProductById(productId);
     }

    // 상품 추가
    public void addProduct(ProductAddRequestDto productAddRequestDto, List<MultipartFile> images){
        ProductCategory productCategory = productCategoryRepository.findById(productAddRequestDto.categoryId())
                .orElseThrow(()-> new IllegalArgumentException("카테고리가 존재하지않음"));

        try{
            Product product = Product.builder()
                    .name(productAddRequestDto.productName())
                    .price(productAddRequestDto.productPrice())
                    .stock(productAddRequestDto.productStock())
                    .category(productCategory)
                    .categoryType(productAddRequestDto.categoryType())
                    .build();

            productRepository.save(product);

            List<String> savedImageUrls = saveImage(images);
            List<ProductImage> productImages = new ArrayList<>();
            for(int i = 0; i < images.size(); i++){
                ProductImage productImage = ProductImage.builder()
                        .image(savedImageUrls.get(i))
                        .orderNo(i + 1)
                        .isMain(i == 0)
                        .product(product)
                        .build();
                productImages.add(productImage);
            }
            productImageRepository.saveAll(productImages);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 이미지를 서버에 저장하고 경로를 반환하는 메서드
    public List<String> saveImage(List<MultipartFile> images) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        List<String> savedImageUrls = new ArrayList<>();

        for(int i = 0; i < images.size(); i++){
            MultipartFile productImg = images.get(i);
            // 파일 이름 생성 (현재 시간 + 원본 파일 이름)
            String currentTime = LocalDateTime.now().format(formatter);
            String fileName = currentTime + "-" + productImg.getOriginalFilename();
            // 저장 경로 생성
            Path filePath = Paths.get(uploadDir, fileName);
            // 경로가 존재하지 않으면 디렉토리 생성
            Files.createDirectories(filePath.getParent());
            // 파일 저장
            Files.copy(productImg.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // 저장된 파일의 상대 경로 반환
            savedImageUrls.add(fileName);
        }
        return savedImageUrls;
    }

    //상품 수정
    @Transactional
    public void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto, List<MultipartFile> images) {
        Product updateProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 상품을 찾을 수 없습니다."));

        ProductCategory updateCategory = productCategoryRepository.findByName(productUpdateRequestDto.categoryName());

        try {
            updateProduct.updateProduct(
                    productId,
                    updateCategory,
                    productUpdateRequestDto.productName(),
                    productUpdateRequestDto.productPrice(),
                    productUpdateRequestDto.productStock(),
                    productUpdateRequestDto.categoryType()
            );
            productImageRepository.deleteByProduct_ProductId(productId);

            List<String> savedImageUrls = saveImage(images);
            List<ProductImage> newProductImages = new ArrayList<>();

            for (int i = 0; i < savedImageUrls.size(); i++) {
                ProductImage productImage = ProductImage.builder()
                        .image(savedImageUrls.get(i))
                        .isMain(i == 0)
                        .orderNo(i + 1)
                        .product(updateProduct)
                        .build();
                newProductImages.add(productImage);
            }
            productImageRepository.saveAll(newProductImages);
            productRepository.save(updateProduct);

        } catch (Exception e) {
            throw new RuntimeException("상품 수정에 실패했습니다.", e);
        }
    }


    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId){
        Product deleteItem = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));
        productRepository.delete(deleteItem);
    }

}
