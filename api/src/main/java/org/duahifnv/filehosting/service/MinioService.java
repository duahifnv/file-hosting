package org.duahifnv.filehosting.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.duahifnv.filehosting.model.FileMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;
    protected static final Logger log = LoggerFactory.getLogger(MinioService.class);

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
            log.debug("Minio: Объект [{}] добавлен в путь [{}/{}]", metaData.getOriginalName(),
                    metaData.getBucket(), metaData.getObjectPath());
        }
    }

    public byte[] downloadObject(FileMeta metaData) throws Exception {
        byte[] objectBytes = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(metaData.getBucket())
                        .object(metaData.getObjectPath())
                        .build()
        ).readAllBytes();
        log.debug("Minio: Объект [{}] загружен из пути [{}/{}]", metaData.getOriginalName(),
                metaData.getBucket(), metaData.getObjectPath());
        return objectBytes;
    }

    public void removeObject(FileMeta metaData) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(metaData.getBucket())
                    .object(metaData.getObjectPath())
                    .build()
        );
        log.debug("Minio: Объект [{}] удален из пути [{}/{}]", metaData.getOriginalName(),
                metaData.getBucket(), metaData.getObjectPath());
    }
}