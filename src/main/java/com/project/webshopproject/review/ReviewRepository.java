package com.project.webshopproject.review;

import com.project.webshopproject.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_ProductId(Long productId);
    void deleteByProduct_ProductId(Long productId);
}
