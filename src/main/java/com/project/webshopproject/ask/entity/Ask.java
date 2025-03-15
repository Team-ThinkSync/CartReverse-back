package com.project.webshopproject.ask.entity;

import com.project.webshopproject.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.project.webshopproject.ask.entity.AskStatus.ANSWERED;

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

    @ManyToOne(fetch = FetchType.LAZY) // Product와 직접적으로 연결
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(length = 500)
    private String adminResponse;

    @Column(length = 500)
    private String answer;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AskStatus askStatus;

    // 모든 필드를 초기화하는 생성자
    public Ask(Long userId, String title, String content, String category, Product product, String imageUrl, String adminResponse) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.product = product;
        this.imageUrl = imageUrl;
        this.adminResponse = adminResponse;
        this.askStatus = ANSWERED; // 기본값 설정
    }

    public Ask(@NotNull(message = "User ID는 null일 수 없습니다.") Long userId,
               @NotBlank(message = "Title은 비어 있을 수 없습니다.") @Size(max = 100, message = "Title의 최대 길이는 100자입니다.") String title,
               @NotBlank(message = "Content는 비어 있을 수 없습니다.") String content,
               @NotBlank(message = "Category는 비어 있을 수 없습니다.") String category,
               Product product) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.product = product;
        this.askStatus = ANSWERED; // 기본 상태 설정
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        this.askStatus = ANSWERED; // 답변이 설정되면 상태를 ANSWERED로 변경
    }

    public void setAdminResponse(String response) {
        this.adminResponse = response;
    }
}
