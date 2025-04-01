package com.project.webshopproject.product;

import com.project.webshopproject.category.entity.ProductCategory;
import com.project.webshopproject.category.repository.ProductCategoryRepository;
import com.project.webshopproject.like.repository.LikeRepository;
import com.project.webshopproject.product.dto.ProductAddRequestDto;
import com.project.webshopproject.product.dto.ProductByCategoryResponseDto;
import com.project.webshopproject.product.dto.ProductFindResponseDto;
import com.project.webshopproject.product.dto.ProductResponseDto;
import com.project.webshopproject.product.dto.ProductUpdateRequestDto;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.entity.ProductImage;
import com.project.webshopproject.product.repository.ProductImageRepository;
import com.project.webshopproject.product.repository.ProductQueryRepository;
import com.project.webshopproject.product.repository.ProductRepository;
import com.project.webshopproject.review.ReviewRepository;
import com.project.webshopproject.s3.S3Service;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductQueryRepository productQueryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;
    private final S3Service s3Service;

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
    public void addProduct(ProductAddRequestDto productAddRequestDto, List<MultipartFile> images) {
        ProductCategory productCategory = productCategoryRepository.findById(productAddRequestDto.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        try {
            Product product = Product.builder()
                    .name(productAddRequestDto.productName())
                    .price(productAddRequestDto.productPrice())
                    .stock(productAddRequestDto.productStock())
                    .category(productCategory)
                    .categoryType(productAddRequestDto.categoryType())
                    .build();

            productRepository.save(product);

            // S3에 이미지 업로드 후 URL 반환
            List<String> savedImageUrls = s3Service.saveImage(images);  // S3 업로드

            // 이미지 정보 DB에 저장
            List<ProductImage> productImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
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

    //상품 수정
    @Transactional
    public void updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto, List<MultipartFile> images) {
        Product updateProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 상품을 찾을 수 없습니다."));

        ProductCategory updateCategory = productCategoryRepository.findByName(productUpdateRequestDto.categoryName());

        try {
            // 상품 정보 수정
            updateProduct.updateProduct(
                    productId,
                    updateCategory,
                    productUpdateRequestDto.productName(),
                    productUpdateRequestDto.productPrice(),
                    productUpdateRequestDto.productStock(),
                    productUpdateRequestDto.categoryType()
            );
            productImageRepository.deleteByProduct_ProductId(productId);  // 기존 이미지 삭제

            // S3에 새 이미지 업로드 후 URL 반환
            List<String> savedImageUrls = s3Service.saveImage(images);  // S3 업로드

            // 새로운 이미지 정보 DB에 저장
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
            productRepository.save(updateProduct);  // 상품 수정

        } catch (Exception e) {
            throw new RuntimeException("상품 수정에 실패했습니다.", e);
        }
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId){
        Product deleteProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        productImageRepository.deleteByProduct_ProductId(productId);

        likeRepository.deleteByProduct_ProductId(productId);

        reviewRepository.deleteByProduct_ProductId(productId);

        productRepository.delete(deleteProduct);
    }

}
