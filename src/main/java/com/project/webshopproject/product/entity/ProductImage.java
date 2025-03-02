package com.project.webshopproject.product.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "product_images")
@Getter
@NoArgsConstructor
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long productImgId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image",length = 255, nullable = false)
    private String image;

    @Column(name = "is_main",nullable = false)
    private Boolean isMain;

    @Column(name = "order_no",nullable = false)
    private Integer orderNo;

    @Builder
    public ProductImage(Long productImgId, String image, Boolean isMain, Integer orderNo, Product product){
        this.productImgId = productImgId;
        this.product = product;
        this.image = image;
        this.isMain = isMain;
        this.orderNo = orderNo;
    }
}
