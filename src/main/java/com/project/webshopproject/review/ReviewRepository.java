package com.project.webshopproject.review;

import com.project.webshopproject.review.entity.Review;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    void deleteByProduct_ProductId(Long productId);
    Page<Review> findByProduct_ProductId(Long productId, Pageable pageable);
}
