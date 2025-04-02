package com.project.webshopproject.product.repository;

import com.project.webshopproject.like.entity.LikeType;
import com.project.webshopproject.like.entity.QLikes;
import com.project.webshopproject.product.dto.*;
import com.project.webshopproject.product.entity.QProductImage;
import com.project.webshopproject.product.entity.QProduct;
import com.project.webshopproject.category.entity.QProductCategory;
import com.project.webshopproject.review.entity.QReview;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public Page<ProductResponseDto> findAllProducts(Pageable pageable){
        List<ProductResponseDto> content = jpaQueryFactory
                .select(new QProductResponseDto(
                product.productId,
                product.category.categoryId,
                product.categoryType.stringValue(),
                product.category.name,
                product.name,
                product.price,
                product.stock,
                productImage.image,
                likes.likesId.count().coalesce(0L)
        )).from(product)
                .leftJoin(productImage)
                .on(productImage.product.productId.eq(product.productId)
                        .and(productImage.isMain.isTrue()))
                .leftJoin(likes)
                .on(likes.product.productId.eq(product.productId)
                        .and(likes.likeType.eq(LikeType.PRODUCT)))  // 좋아요 테이블 조인 + 필터링
                .groupBy(product.productId, productImage.image) // 중복 방지
                .offset(pageable.getOffset()) // 시작 위치
                .limit(pageable.getPageSize()) // 가져올 데이터 개수
                .fetch();
      
         long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .fetchOne();
      
        return new PageImpl<>(content, pageable,total);

    }
    //세부 상품 조회
    public ProductFindResponseDto findProductById(Long productId) {
        ProductFindResponseDto productFindResponseDto = jpaQueryFactory
                .select(new QProductFindResponseDto(
                        product.name,
                        product.price,
                        product.stock,
                        product.categoryType.stringValue(),
                        product.category.name,
                        likes.likesId.count().coalesce(0L)
                ))
                .from(product)
                .leftJoin(likes)
                .on(likes.product.productId.eq(product.productId)
                        .and(likes.likeType.eq(LikeType.PRODUCT)))
                .where(product.productId.eq(productId))
                .groupBy(product.productId)
                .fetchOne();

        // 이미지 리스트 따로 조회
        List<String> images = jpaQueryFactory
                .select(productImage.image)
                .from(productImage)
                .where(productImage.product.productId.eq(productId))
                .fetch();

        // DTO에 이미지 리스트 추가
        productFindResponseDto.setProductImages(images);

        return productFindResponseDto;
    }

    //카테 고리 별 조회
    public Page<ProductByCategoryResponseDto> getProductByCategory(Long categoryId, Pageable pageable){
        List<ProductByCategoryResponseDto> content = jpaQueryFactory
                .select(new QProductByCategoryResponseDto(
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(product.category.categoryId.eq(categoryId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    //카테고리 삭제할때, 관련된 상품들도 삭제하게끔
    public void deleteProductByCategory(Long categoryId){

        jpaQueryFactory.delete(productImage)
                .where(productImage.product.category.categoryId.eq(categoryId))
                .execute();

        jpaQueryFactory.delete(product)
                .where(product.category.categoryId.eq(categoryId))
                .execute();

        jpaQueryFactory.delete(productCategory)
                .where(productCategory.categoryId.eq(categoryId))
                .execute();
    }

}
