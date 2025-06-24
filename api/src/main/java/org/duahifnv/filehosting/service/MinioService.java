package org.duahifnv.filehosting.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.duahifnv.filehosting.model.FileMeta;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void uploadObject(FileMeta metaData, byte[] bytes) throws Exception {
        try (var byteInput = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(metaData.getBucket())
                    .object(metaData.getObjectPath())
                    .stream(byteInput, bytes.length, -1)
                    .contentType(metaData.getContentType())
                    .build()
            );
        }
    }

    public byte[] downloadObject(FileMeta metaData) throws Exception {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(metaData.getBucket())
                .object(metaData.getObjectPath())
                .build()
        ).readAllBytes();
    }

    public void removeObject(FileMeta fileMeta) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(fileMeta.getBucket())
                    .object(fileMeta.getObjectPath())
                    .build()
        );
    }
}