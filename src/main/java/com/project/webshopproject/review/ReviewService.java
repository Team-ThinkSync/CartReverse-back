package com.project.webshopproject.review;

import com.project.webshopproject.like.entity.LikeType;
import com.project.webshopproject.like.repository.LikeRepository;
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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final LikeRepository likeRepository;
    // 이미지 저장 경로 (Docker Volume 마운트 경로)
    @Value("${file.upload-dir}")
    private String imageUploadDir;

    @Transactional
    public void createReview(Long productId, ReviewRequestDto requestDto, List<MultipartFile> images, User user) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error("제품을 찾지 못함 | request : {}", productId);
            return new IllegalArgumentException("제품을 찾지 못했습니다.");
        });

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

        if (images == null || images.isEmpty()) {
            log.warn("이미지 없이 리뷰가 등록됨 | reviewId: {}", review.getReviewId());
            return;
        }

        int orderNo = 1;
        for (MultipartFile image : images) {
            try {
                String imageUrl = saveImage(image);
                ReviewImage reviewImage = ReviewImage.builder()
                        .review(review)
                        .image(imageUrl)
                        .orderNo(orderNo++)
                        .build();
                reviewImageRepository.save(reviewImage);
            } catch (Exception e) {
                log.error("이미지 저장 실패 | reviewId: {}, error: {}", review.getReviewId(), e.getMessage());
                throw new IllegalStateException("이미지 저장 중 오류가 발생했습니다.");
            }
        }
    }


//    public List<ReviewResponseDto> getAllReviews(Long productId) {
//        return reviewRepository.findByProduct_ProductId(productId)
//                .stream()
//                .map(review -> new ReviewResponseDto(
//                        review.getReviewId(),
//                        review.getUser().getUserId(),
//                        review.getProduct().getProductId(),
//                        review.getTitle(),
//                        review.getContent(),
//                        review.getRate(),
//                        review.getCreatedAt(),
//                        review.getReviewImages().stream()
//                                .sorted(Comparator.comparingInt(ReviewImage::getOrderNo)) // orderNo 기준 정렬
//                                .map(ReviewImage::getImage)
//                                .collect(Collectors.toList())
//
//                ))
//                .collect(Collectors.toList());
//    }

    public List<ReviewResponseDto> getAllReviews(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId)
                .stream()
                .map(review -> {
                    // 각 리뷰에 대해 좋아요 수를 개별적으로 계산
                    Long likeCount = likeRepository.countByReviewId(review.getReviewId());

                    return new ReviewResponseDto(
                            review.getReviewId(),
                            review.getUser().getUserId(),
                            review.getProduct().getProductId(),
                            review.getTitle(),
                            review.getContent(),
                            review.getRate(),
                            review.getCreatedAt(),
                            review.getReviewImages().stream()
                                    .sorted(Comparator.comparingInt(ReviewImage::getOrderNo)) // orderNo 기준 정렬
                                    .map(ReviewImage::getImage)
                                    .collect(Collectors.toList()),
                            likeCount // 각 리뷰에 대한 좋아요 수 추가
                    );
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto requestDto, List<MultipartFile> newImages, List<Long> deleteImageIds) {
        // 리뷰 찾기
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new IllegalArgumentException("Review not found"));

        // 제목, 내용, 별점 중 변경된 값만 업데이트
        if (requestDto.title() != null) review.updateTitle(requestDto.title());
        if (requestDto.content() != null) review.updateContent(requestDto.content());
        if (requestDto.rate() != null) {
            review.updateRate(requestDto.rate());
        }

        // 삭제할 이미지 처리
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            reviewImageRepository.deleteAllById(deleteImageIds);
        }

        // 새 이미지 추가
        if (newImages != null && !newImages.isEmpty()) {
            int nextOrderNo = review.getReviewImages().size() + 1;
            for (MultipartFile image : newImages) {
                String imageUrl = saveImage(image);
                ReviewImage reviewImage = ReviewImage.builder()
                        .review(review)
                        .image(imageUrl)
                        .orderNo(nextOrderNo++)
                        .build();
                reviewImageRepository.save(reviewImage);
            }
        }

        // 최신 리뷰 이미지 리스트 가져오기 (orderNo 기준 정렬)
        List<String> imageUrls = review.getReviewImages().stream()
                .sorted(Comparator.comparingInt(ReviewImage::getOrderNo))
                .map(ReviewImage::getImage)
                .collect(Collectors.toList());

        Long likeCount = likeRepository.countByReviewId(reviewId);

        // ReviewResponseDto 반환
        return new ReviewResponseDto(
                review.getReviewId(),
                review.getUser().getUserId(),
                review.getProduct().getProductId(),
                review.getTitle(),
                review.getContent(),
                review.getRate(),
                review.getCreatedAt(),
                imageUrls,
                likeCount
        );
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
