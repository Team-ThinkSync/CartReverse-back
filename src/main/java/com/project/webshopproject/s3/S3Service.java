package com.project.webshopproject.s3;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client amazonS3;
    private final String bucketName;
    private final String region;

    public S3Service(
            S3Client amazonS3,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.region}") String region
    ) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.region = region;
    }

    public String saveImage(MultipartFile image) {
        return uploadToS3(image);
    }

    public List<String> saveImage(List<MultipartFile> images) {
        return images.stream()
                .map(this::uploadToS3)
                .collect(Collectors.toList());
    }

    private String uploadToS3(MultipartFile image) {
        try {
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType(image.getContentType())
                    .build();

            amazonS3.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
            );

            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, filename);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장에 실패했습니다.", e);
        }
    }
}
