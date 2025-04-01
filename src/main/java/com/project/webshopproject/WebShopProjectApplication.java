package com.project.webshopproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //이부분 꼭 추가해야함
public class WebShopProjectApplication {

    public static void main(String[] args) {

        // Spring 애플리케이션 실행
        SpringApplication.run(WebShopProjectApplication.class, args);
    }
}
