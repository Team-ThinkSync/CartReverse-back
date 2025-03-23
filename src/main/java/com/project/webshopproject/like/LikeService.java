package com.project.webshopproject.like;

import com.project.webshopproject.like.dto.LikeRequestDto;
import com.project.webshopproject.like.entity.LikeType;
import com.project.webshopproject.like.entity.Likes;
import com.project.webshopproject.like.repository.LikeRepository;
import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.repository.ProductRepository;
import com.project.webshopproject.review.ReviewRepository;
import com.project.webshopproject.review.entity.Review;
import com.project.webshopproject.user.UserRepository;
import com.project.webshopproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void toggleLike(User user,LikeRequestDto likeRequestDto) {

        if (likeRequestDto.likeType() == LikeType.PRODUCT) {
            Product product = productRepository.findById(likeRequestDto.targetId())
                    .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

            Optional<Likes> existingLike = likeRepository.findByUser_UserIdAndProduct_ProductId(user.getUserId(), product.getProductId());

            existingLike.ifPresentOrElse(
                    likeRepository::delete, // 값이 있으면 삭제
                    () -> likeRepository.save(new Likes(user, product)) // 값이 없으면 저장
            );
        }

        if (likeRequestDto.likeType() == LikeType.REVIEW) {
            Review review = reviewRepository.findById(likeRequestDto.targetId())
                    .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));
            Optional<Likes> existingLike = likeRepository.findByUser_UserIdAndReview_ReviewId(user.getUserId(), review.getReviewId());

            existingLike.ifPresentOrElse(
                    likeRepository::delete, // 값이 있으면 삭제
                    () -> likeRepository.save(new Likes(user, review)) // 값이 없으면 저장
            );
        }
    }
}
