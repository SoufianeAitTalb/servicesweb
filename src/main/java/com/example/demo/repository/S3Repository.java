package com.example.demo.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class S3Repository {

    private static final String BUCKET = "cours-m2";

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    // Dépose un fichier dans S3 depuis le backend (le remplace s'il existe déjà)
    public void uploadFile(String key, byte[] content, String contentType) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(content));
    }

    // Lien temporaire pour lire un fichier directement dans S3 (GET)
    public String getFileUrl(String filePath) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(filePath)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(request)
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toExternalForm();
    }

    // Lien temporaire pour déposer un fichier directement dans S3 (PUT)
    public String getUploadFileUrl(String key) {
        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(PutObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(key)
                        .build())
                .build();
        return s3Presigner.presignPutObject(request)
                .url()
                .toExternalForm();
    }
}
