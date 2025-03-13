package com.project.webshopproject.like.entity;

import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.review.entity.Review;
import com.project.webshopproject.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@Getter
@NoArgsConstructor
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long likesId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = true)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(optional = true)
    @JoinColumn(name = "review_id")
    private Review review;

    @Enumerated(EnumType.STRING)
    @Column(name="likesType",nullable = false, length = 10)
    private LikeType likeType;

    public Likes(User user, Product product) {
        this.user = user;
        this.product = product;
        this.likeType = LikeType.PRODUCT;
    }

    public Likes(User user, Review review) {
        this.user = user;
        this.review = review;
        this.likeType = LikeType.REVIEW;
    }


}
