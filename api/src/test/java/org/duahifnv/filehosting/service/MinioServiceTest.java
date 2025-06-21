package org.duahifnv.filehosting.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MinioServiceTest {
    @InjectMocks
    private MinioService minioService;

    @Mock
    private MinioClient minioClient;

    @Test
    public void uploadObject_shouldCallMinioClient() throws Exception {
        // given
        var bucketName = "test-bucket";
        var objectName = "test-object";
        var inputStream = new ByteArrayInputStream("test-content".getBytes());
        var size = 10;
        var contentType = "text/plain";

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        // when
        minioService.uploadObject(bucketName, objectName, inputStream, size, contentType);

        // then
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void downloadObject_shouldCallMinioClient() throws Exception {
        // given
        var bucketName = "test-bucket";
        var objectName = "test-object";
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(null);

        // when
        var result = minioService.downloadObject(bucketName, objectName);

        // then
        verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
    }
}