package com.project.webshopproject.model;

import jakarta.persistence.*;

import static com.project.webshopproject.model.Status.ANSWERED;

@Entity
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
    private Status status;

    @Column(length = 20)
    private String ImageUrl;

    // 모든 필드를 초기화하는 생성자 추가
    public Ask(Long userId, String title, String content, String category, String itemId, String ImageUrl) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.itemId = Long.valueOf(itemId);
        this.status = ANSWERED; // 기본값 설정
        this.ImageUrl = ImageUrl;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public String getAnswer() {
        return answer;
    }

    public Status getStatus() {
        return status;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    // 응답 설정 메서드
    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        this.status = ANSWERED; // 답변이 설정되면 상태를 ANSWERED로 변경
    }
}
