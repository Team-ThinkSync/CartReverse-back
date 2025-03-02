package com.project.webshopproject.ask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class AskRequestDto {

    // Getters only, no setters
    @NotNull(message = "User ID는 null일 수 없습니다.")
    private final Long userID;

    @NotBlank(message = "Title은 비어 있을 수 없습니다.")
    @Size(max = 100, message = "Title의 최대 길이는 100자입니다.")
    private final String title;

    @NotBlank(message = "Content는 비어 있을 수 없습니다.")
    private final String content;

    @NotBlank(message = "Category는 비어 있을 수 없습니다.")
    private final String category;

    @Pattern(regexp = "^[A-Za-z0-9]{3,10}$", message = "Item ID는 3자에서 10자 사이의 영숫자여야 합니다.")
    private final String itemId;

    public AskRequestDto(@NotNull(message = "User ID는 null일 수 없습니다.") Long userID,
                         @NotBlank(message = "Title은 비어 있을 수 없습니다.") @Size(max = 100, message = "Title의 최대 길이는 100자입니다.") String title,
                         @NotBlank(message = "Content는 비어 있을 수 없습니다.") String content,
                         @NotBlank(message = "Category는 비어 있을 수 없습니다.") String category,
                         @Pattern(regexp = "^[A-Za-z0-9]{3,10}$", message = "Item ID는 3자에서 10자 사이의 영숫자여야 합니다.") String itemId) {
        this.userID = userID;
        this.title = title;
        this.content = content;
        this.category = category;
        this.itemId = itemId;
    }

}
