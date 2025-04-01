package com.project.webshopproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Bean
    public S3Client amazonS3() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    // S3 버킷 이름을 다른 클래스에서 사용할 수 있도록 주입
    @Bean
    public String getBucketName() {
        return bucketName;
    }

    @Bean
    public String getRegion() {
        return region;
    }
}
