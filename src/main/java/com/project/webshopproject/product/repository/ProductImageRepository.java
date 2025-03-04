package com.project.webshopproject.product.repository;

import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    void deleteByProduct_ProductId(Long productId); // 객체로 서칭 x  아이디로 서칭
}
