package com.project.webshopproject.like.repository;

import com.project.webshopproject.like.entity.Likes;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.review.entity.Review;
import com.project.webshopproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    // 특정 사용자의 좋아요 찾기
    Optional<Likes> findByUser_UserIdAndProduct_ProductId(Long userId, Long productId);
    Optional<Likes> findByUser_UserIdAndReview_ReviewId(Long userId, Long reviewId);

    void deleteByProduct_ProductId(Long productId);
}