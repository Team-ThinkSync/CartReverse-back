package com.project.webshopproject.ask.dto;

import com.project.webshopproject.ask.entity.Ask;
import com.project.webshopproject.ask.entity.AskImage;
import com.project.webshopproject.ask.entity.Category;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AskResponseDto {
    private final Long askId;
    private final Long userId;
    private final String title;
    private final String content;
    private final Category category;
    private final Long productId;
    private final String answer;
    private final List<String> imageUrls;

    public AskResponseDto(Long askId, Long userId, String title, String content, Category category, Long productId, String answer, List<String> imageUrls) {
        this.askId = askId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.productId = productId;
        this.answer = answer;
        this.imageUrls = imageUrls;
    }

    // Ask 엔티티 -> DTO 변환 생성자 추가
    public AskResponseDto(Ask ask) {
        this.askId = ask.getAskId();
        this.userId = ask.getUserId();
        this.title = ask.getTitle();
        this.content = ask.getContent();
        this.category = (Category) ask.getCategory();
        this.productId = ask.getProduct().getProductId();
        this.answer = ask.getAnswer();
        this.imageUrls = ask.getImages().stream()
                .map(AskImage::getImageUrl) // AskImage 객체에서 이미지 URL 추출
                .collect(Collectors.toList());
    }
}
