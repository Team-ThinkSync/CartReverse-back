package com.project.webshopproject.product.repository;

import com.project.webshopproject.product.dto.*;
import com.project.webshopproject.product.entity.QProductImage;
import com.project.webshopproject.product.entity.QProduct;
import com.project.webshopproject.category.entity.QProductCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QProduct product = QProduct.product;
    private final QProductImage productImage = QProductImage.productImage;
    private final QProductCategory productCategory = QProductCategory.productCategory;

    // 모든 상품 조회
    public List<ProductResponseDto> findAllProducts(){
        return jpaQueryFactory
                .select(new QProductResponseDto(
                        product.productId,
                        product.category.categoryId,
                        product.categoryType.stringValue(),
                        productCategory.name,
                        product.name,
                        productImage.image,
                        product.price,
                        product.stock
                ))
                .from(product)
                .leftJoin(product.category, productCategory)
                .leftJoin(productImage)
                .on(product.eq(productImage.product)
                        .and(productImage.isMain.isTrue())) // 메인이미지만 필터링
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
                        productCategory.name
                ))
                .from(product)
                .leftJoin(product.category, productCategory)
                .leftJoin(productImage).on(productImage.product.eq(product)) // 상품 이미지와 연관관계 매핑
                .where(product.productId.eq(productId)) // 특정 상품 ID로 필터링
                .fetchOne();
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
    //상품 삭제시 이미지도 삭제
    public void deleteProduct(Long productId){

        jpaQueryFactory.delete(productImage)
                .where(productImage.product.productId.eq(productId))
                .execute();

//        jpaQueryFactory.delete(product)
//                .where(product.productId.eq(productId))
//                .execute();
    }
}
