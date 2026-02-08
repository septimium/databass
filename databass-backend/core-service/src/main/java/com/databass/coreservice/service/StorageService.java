package com.databass.coreservice.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    public StorageService(@Value("${minio.url}") String url,
                          @Value("${minio.access-key}") String accessKey,
                          @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadSong(byte[] audioData, Long songId) {
        String fileName = "song-" + songId + ".mp3";

        try {
            InputStream stream = new ByteArrayInputStream(audioData);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(stream, audioData.length, -1)
                            .contentType("audio/mpeg")
                            .build()
            );

            return minioUrl + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to MinIO", e);
        }
    }
}