package com.project.webshopproject.category;

import com.project.webshopproject.category.dto.CategoryAddRequestDto;
import com.project.webshopproject.category.dto.CategoryEditRequestDto;
import com.project.webshopproject.category.dto.CategoryResponseDto;
import com.project.webshopproject.category.entity.ProductCategory;
import com.project.webshopproject.category.repository.ProductCategoryRepository;
import com.project.webshopproject.like.repository.LikeRepository;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.repository.ProductImageRepository;
import com.project.webshopproject.product.repository.ProductQueryRepository;
import com.project.webshopproject.product.repository.ProductRepository;
import com.project.webshopproject.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductQueryRepository productQueryRepository;
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;


    // 카테고리 전체 조회
    public List<CategoryResponseDto> getAllCategories(){
        List<ProductCategory> allCategories = productCategoryRepository.findAll();
        return allCategories.stream()
                .map(productCategory -> new CategoryResponseDto(
                productCategory.getCategoryId(),
                productCategory.getName())).collect(Collectors.toList());
    }
    // 카테고리 추가
    public void addCategory(CategoryAddRequestDto categoryAddRequestDto){
        ProductCategory productCategory = ProductCategory.builder()
                .name(categoryAddRequestDto.categoryName()).build();
        productCategoryRepository.save(productCategory);
    }

    // 카테고리 수정
    public void editCategory(Long categoryId,CategoryEditRequestDto categoryEditRequestDto){
        ProductCategory productCategory = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        productCategory = productCategory.builder()
                .id(categoryId)
                .name(categoryEditRequestDto.name())
                .build();

        productCategoryRepository.save(productCategory);
    }

    // 카테고리 삭제
    public void deleteCategory(Long categoryId){

        List<Product> products = productRepository.findByCategoryId(categoryId);

        for (Product product : products) {
            Long productId = product.getProductId();
            likeRepository.deleteByProduct_ProductId(productId);
            productImageRepository.deleteByProduct_ProductId(productId);
            reviewRepository.deleteByProduct_ProductId(productId);
        }
        productCategoryRepository.deleteById(categoryId);
    }
}
