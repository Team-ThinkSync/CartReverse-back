package com.project.webshopproject.review;

import com.project.webshopproject.common.RestApiResponseDto;
import com.project.webshopproject.review.dto.ReviewRequestDto;
import com.project.webshopproject.review.dto.ReviewResponseDto;
import com.project.webshopproject.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ReviewRestController {
    private final ReviewService reviewService;

    /**
     * 리뷰 추가
     * @param productId 제품 고유번호
     * @param requestDto 제목, 내용, 별점
     * @param images 이미지
     * @param userDetails
     */
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<RestApiResponseDto<String>> createReview(
            @PathVariable final Long productId,
            @RequestPart("dto") final ReviewRequestDto requestDto,
            @RequestPart("images") final List<MultipartFile> images,
            @AuthenticationPrincipal final UserDetailsImpl userDetails
    ) {
        reviewService.createReview(productId, requestDto, images, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestApiResponseDto.of("리뷰가 등록되었습니다."));
    }

    /**
     * 리뷰 전체조회
     * @param productId 제품 고유번호
     */
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<RestApiResponseDto<List<ReviewResponseDto>>> getAllReviews(
            @PathVariable Long productId
    ) {
        List<ReviewResponseDto> responseDto = reviewService.getAllReviews(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestApiResponseDto.of("리뷰를 전체조회하였습니다.", responseDto));
    }

    /**
     * 리뷰 수정
     * @param reviewId 리뷰 고유번호
     * @param requestDto 제목, 내용, 별점
     */
    @PatchMapping("/products/{productId}/reviews/{reviewId}")
    public ResponseEntity<RestApiResponseDto<String>> updateReview(
            @PathVariable Long reviewId,
            @RequestPart("dto") ReviewRequestDto requestDto) {
        reviewService.updateReview(reviewId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestApiResponseDto.of("리뷰가 수정되었습니다."));
    }

    /**
     * 리뷰 삭제
     * @param reviewId 리뷰 고유번호
     */
    @DeleteMapping("/products/{productId}/reviews/{reviewId}")
    public ResponseEntity<RestApiResponseDto<String>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestApiResponseDto.of("리뷰가 삭제되었습니다."));
    }
}