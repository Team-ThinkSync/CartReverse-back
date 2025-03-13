package com.project.webshopproject.ask.entity;

import static com.project.webshopproject.ask.entity.AskStatus.ANSWERED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "asks")
@NoArgsConstructor
public class Ask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 20)
    private Long itemId;

    @Column(length = 500)
    private String adminResponse;

    @Column(length = 500)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AskStatus askStatus;

    // 모든 필드를 초기화하는 생성자 추가
    public Ask(Long userId, String title, String content, String category, Long itemId) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.itemId = itemId;
        this.askStatus = ANSWERED; // 기본값 설정
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        this.askStatus = ANSWERED; // 답변이 설정되면 상태를 ANSWERED로 변경
    }
}
