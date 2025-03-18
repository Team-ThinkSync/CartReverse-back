package com.project.webshopproject.ask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AskRequestDto {

    @NotNull(message = "User ID는 null일 수 없습니다.")
    private final Long userId;

    @NotBlank(message = "Title은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "Title의 최대 길이는 100자입니다.")
    private final String title;

    @NotBlank(message = "Content는 비어 있을 수 없습니다.")
    private final String content;

    @NotBlank(message = "Category는 비어 있을 수 없습니다.")
    private final Enum category;

    @NotNull(message = "Product ID는 null일 수 없습니다.")
    private final Long productId;

    private List<String> imageUrls;
}
