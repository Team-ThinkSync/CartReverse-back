package com.project.webshopproject.category.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="productCategory")
@Getter
@NoArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name",nullable = false, length = 255)
    private String name;

    @Builder
    public ProductCategory(Long id, String name) {
        this.categoryId = id;
        this.name = name;
    }

}
