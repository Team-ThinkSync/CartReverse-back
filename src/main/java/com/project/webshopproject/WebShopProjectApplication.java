package com.project.webshopproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WebShopProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebShopProjectApplication.class, args);
    }

}
