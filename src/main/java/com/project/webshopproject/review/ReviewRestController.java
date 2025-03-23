package com.project.webshopproject.review;

import com.project.webshopproject.common.RestApiResponseDto;
import com.project.webshopproject.review.dto.ReviewRequestDto;
import com.project.webshopproject.review.dto.ReviewResponseDto;
import com.project.webshopproject.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ReviewRestController {
    private final ReviewService reviewService;

    /**
     * 리뷰 추가
     * @param productId: 제품 고유번호
     * @param requestDto: title, content, rate
     * @param images: 이미지 파일
     * @param userDetails: 유저 객체
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
     * @param productId: 제품 고유번호
     */
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<RestApiResponseDto<Page<ReviewResponseDto>>> getAllReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<ReviewResponseDto> responseDto = reviewService.getAllReviews(productId, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestApiResponseDto.of("리뷰를 전체조회하였습니다.", responseDto));
    }

    /**
     * 리뷰 수정
     * @param reviewId: 리뷰 고유번호
     * @param requestDto: title, content, rate
     */
    @PatchMapping("/products/{productId}/reviews/{reviewId}")
    public ResponseEntity<RestApiResponseDto<String>> updateReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @Valid @RequestPart("dto") ReviewRequestDto requestDto, // @Valid로 검증 활성화
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "deleteImageIds", required = false) List<Long> deleteImageIds) {

        reviewService.updateReview(reviewId, requestDto, newImages, deleteImageIds);

        return ResponseEntity.status(HttpStatus.OK)
                .body(RestApiResponseDto.of("리뷰가 수정되었습니다."));
    }

    /**
     * 리뷰 삭제
     * @param reviewId: 리뷰 고유번호
     */
    @DeleteMapping("/products/{productId}/reviews/{reviewId}")
    public ResponseEntity<RestApiResponseDto<String>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RestApiResponseDto.of("리뷰가 삭제되었습니다."));
    }
}