package com.project.webshopproject.ask.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ask_images")
@NoArgsConstructor
public class AskImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long askImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ask_id", nullable = false)
    private Ask ask;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    public AskImage(Ask ask, String imageUrl) {
        this.ask = ask;
        this.imageUrl = imageUrl;
    }
}
