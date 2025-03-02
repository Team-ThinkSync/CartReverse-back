package com.project.webshopproject.ask.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AskResponseDto {
    private final Long id;
    private final Long userId;
    private final String title;
    private final String content;
    private final String category;
    private final Long itemId;
}