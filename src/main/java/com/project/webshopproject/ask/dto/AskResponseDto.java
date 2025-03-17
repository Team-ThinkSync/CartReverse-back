package com.project.webshopproject.ask.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AskResponseDto {
    private final Long askId;
    private final Long userId;
    private final String title;
    private final String content;
    private final String category;
    private final Long productId;
    private final String Answer;
    private List<String> imageUrls;

    public AskResponseDto(Long askId, Long userId, String title, String content, String category, Long productId, String Answer, List<String> imageUrls) {
        this.askId = askId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.productId = productId;
        this.Answer = Answer;
        this.imageUrls = imageUrls;

    }
}
