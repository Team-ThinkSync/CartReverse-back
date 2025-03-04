package com.project.webshopproject.product.entity;

import com.project.webshopproject.category.entity.CategoryType;
import com.project.webshopproject.category.entity.ProductCategory;
import com.project.webshopproject.product.dto.ProductUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name="categoryType",nullable = false, length = 10)
    private CategoryType categoryType;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Builder
    public Product(Long productId, ProductCategory category, String name, Integer price,
                   Integer stock, CategoryType categoryType) {
        this.productId = productId;
        this.category = category;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryType = categoryType;
    }
    public void updateProduct(Long productId, ProductCategory category, String name, Integer price,
                              Integer stock, CategoryType categoryType) {
        this.productId = productId;
        this.category = category;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryType = categoryType;
    }

}
