package com.project.webshopproject.review;

import com.project.webshopproject.product.entity.Product;
import com.project.webshopproject.product.repository.ProductRepository;
import com.project.webshopproject.review.dto.ReviewRequestDto;
import com.project.webshopproject.review.dto.ReviewResponseDto;
import com.project.webshopproject.review.entity.Review;
import com.project.webshopproject.review.entity.ReviewImage;
import com.project.webshopproject.user.entity.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j(topic = "Review Service")
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductRepository productRepository;
    // 이미지 저장 경로 (Docker Volume 마운트 경로)
    @Value("${file.upload-dir}")
    private String imageUploadDir;

    public void createReview(Long productId, ReviewRequestDto requestDto, List<MultipartFile> images, User user) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error("제품을 찾지 못함 | request : {}", productId);
            return new IllegalArgumentException("제품을 찾지 못했습니다.");
        });;
        Review review = Review.builder()
                .product(product)
                .user(user)
                .title(requestDto.title())
                .content(requestDto.content())
                .rate(requestDto.rate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        int orderNo = 1;
        for (MultipartFile image : images) {
            // 파일 저장 로직 (예시)
            String imageUrl = saveImage(image); // 실제 파일 저장 및 URL 반환 로직 필요

            // ReviewImage 엔티티 생성 및 저장
            ReviewImage reviewImage = ReviewImage.builder()
                    .review(review) // 저장된 리뷰의 ID 설정
                    .image(imageUrl)
                    .orderNo(orderNo++) // 순서대로 번호 부여
                    .build();
            reviewImageRepository.save(reviewImage);
        }
    }

    public List<ReviewResponseDto> getAllReviews(Long productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(review -> new ReviewResponseDto(
                        review.getReviewId(),
                        review.getUser().getUserId(),
                        review.getProduct().getProductId(),
                        review.getTitle(),
                        review.getContent(),
                        review.getRate(),
                        review.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new IllegalArgumentException("Review not found"));
        review.updateReview(requestDto.title(), requestDto.content(), requestDto.rate(), LocalDateTime.now());
        return new ReviewResponseDto(
                review.getReviewId(),
                review.getUser().getUserId(),
                review.getProduct().getProductId(),
                review.getTitle(),
                review.getContent(),
                review.getRate(),
                review.getCreatedAt());
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // 실제 파일 저장 및 URL 반환 로직 (구현 필요)
    private String saveImage(MultipartFile image) {
        try {
            // 이미지 파일 이름 생성 (UUID 사용)
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 이미지 저장 경로 생성
            Path imagePath = Paths.get(imageUploadDir, filename);

            // 이미지 저장
            Files.copy(image.getInputStream(), imagePath);

            // 이미지 URL 반환 (Docker Volume 경로 + 파일 이름)
            return "/images/" + filename; // Controller에서 접근 가능한 URL
        } catch (IOException e) {
            // 파일 저장 실패 시 예외 처리
            throw new RuntimeException("이미지 저장에 실패했습니다.", e);
        }
    }
}
