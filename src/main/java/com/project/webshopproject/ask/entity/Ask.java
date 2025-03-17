package com.project.webshopproject.ask.entity;

import com.project.webshopproject.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "asks")
@NoArgsConstructor
public class Ask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long askId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, length = 50)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(length = 500)
    private String adminResponse;

    @Column(length = 500)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AskStatus askStatus;

    // 이미지 테이블과 연관 관계 설정 (일대다)
    @OneToMany(mappedBy = "ask", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AskImage> images;

    // 생성자에서 유효성 검사를 통해 값을 받도록 변경
    public Ask(Long userId, String title, String content, String category, Product product, String adminResponse) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.product = product;
        this.adminResponse = adminResponse;
        this.askStatus = AskStatus.ANSWERED;
    }

    public Ask(Long userId, String title, String content, String category, Product product) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.product = product;
        this.askStatus = AskStatus.WAITING; // 기본값으로 설정
    }

    // 답변과 상태 변경 메소드
    public void setAnswer(String answer, String response) {
        this.answer = answer;
        this.askStatus = AskStatus.ANSWERED;
        this.adminResponse = response;
    }

    // 이미지 추가 메소드
    public void addImages(List<AskImage> images) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.addAll(images);
    }

    // 이미지 삭제 메소드
    public void removeImage(AskImage image) {
        if (this.images != null) {
            this.images.remove(image);
        }
    }

    // 이거 Service 로직에서 에러남 (이거 삭제하면)
    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title은 비어 있을 수 없습니다.");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Title의 최대 길이는 100자입니다.");
        }
        this.title = title;
    }

    public void setContent(String content) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content는 비어 있을 수 없습니다.");
        }
        this.content = content;
    }

}
