package com.project.webshopproject.product.repository;

import com.project.webshopproject.like.entity.LikeType;
import com.project.webshopproject.like.entity.QLikes;
import com.project.webshopproject.product.dto.*;
import com.project.webshopproject.product.entity.QProductImage;
import com.project.webshopproject.product.entity.QProduct;
import com.project.webshopproject.category.entity.QProductCategory;
import com.project.webshopproject.review.entity.QReview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
@Transactional
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QProduct product = QProduct.product;
    private final QProductImage productImage = QProductImage.productImage;
    private final QProductCategory productCategory = QProductCategory.productCategory;
    private final QLikes likes = QLikes.likes;
    private final QReview review = QReview.review;

     //모든 상품 조회
    public List<ProductResponseDto> findAllProducts(){
        return jpaQueryFactory.select(new QProductResponseDto(
                product.productId,
                product.category.categoryId,
                product.categoryType.stringValue(),
                product.category.name,
                product.name,
                product.price,
                product.stock,
                productImage.image,
                likes.likesId.count()
        )).from(product)
                .leftJoin(productImage)
                .on(productImage.product.productId.eq(product.productId)
                        .and(productImage.isMain.isTrue()))
                .leftJoin(likes)
                .on(likes.product.productId.eq(product.productId)
                        .and(likes.likeType.eq(LikeType.PRODUCT)))  // 좋아요 테이블 조인 + 필터링
                .groupBy(product.productId, productImage.image) // 중복 방지
                .fetch();
    }
    //세부 상품 조회
    public ProductFindResponseDto findProductById(Long productId){
        return jpaQueryFactory
                .select(new QProductFindResponseDto(
                        product.name,
                        product.price,
                        product.stock,
                        productImage.image,
                        product.categoryType.stringValue(),
                        productCategory.name,
                        likes.likesId.count()
                ))
                .from(product)
                .leftJoin(product.category, productCategory)
                .leftJoin(productImage).on(productImage.product.eq(product)) // 상품 이미지와 연관관계 매핑
                .leftJoin(likes)
                .on(likes.product.productId.eq(product.productId)
                        .and(likes.likeType.eq(LikeType.PRODUCT)))  // 좋아요 테이블 조인 + 필터링
                .groupBy(product.productId, productImage.image) // 중복 방지
                .leftJoin(review)
                .on(review.product.productId.eq(product.productId)
                        .and(likes.likeType.eq(LikeType.REVIEW)))
                .groupBy(product.productId, productImage.image)
                .where(product.productId.eq(productId)) // 특정 상품 ID로 필터링
                .fetchOne();
    }

    //카테 고리 별 조회
    public List<ProductByCategoryResponseDto> getProductByCategory(Long categoryId){
        return jpaQueryFactory.select(new QProductByCategoryResponseDto(
                product.name,
                product.price,
                product.stock,
                productImage.image
        ))
                .from(product)
                .leftJoin(productImage)
                .on(productImage.product.productId.eq(product.productId)
                        .and(productImage.isMain.isTrue()))
                .where(product.category.categoryId.eq(categoryId))
                .fetch();
    }

}
