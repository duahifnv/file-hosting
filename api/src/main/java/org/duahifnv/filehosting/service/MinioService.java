package org.duahifnv.filehosting.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void uploadObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws Exception {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build()
        );
    }

    public InputStream downloadObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build()
        );
    }
}