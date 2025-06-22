package org.duahifnv.filehosting.service;

import io.minio.*;
import org.duahifnv.filehosting.model.FileMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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
        var fileMeta = mock(FileMeta.class);
        var fileBytes = new byte[] {-10, 42, 52};

        when(fileMeta.getBucket()).thenReturn("test-bucket");
        when(fileMeta.getObjectPath()).thenReturn("test-object");
        when(fileMeta.getSize()).thenReturn(10L);
        when(fileMeta.getContentType()).thenReturn("text/plain");

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        // when
        minioService.uploadObject(fileMeta, fileBytes);

        // then
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    public void downloadObject_shouldCallMinioClient() throws Exception {
        // given
        var fileMeta = mock(FileMeta.class);
        var fileBytes = new byte[] {-10, 42, 52};

        when(fileMeta.getBucket()).thenReturn("test-bucket");
        when(fileMeta.getObjectPath()).thenReturn("test-object");

        var objectResponse = mock(GetObjectResponse.class);
        when(objectResponse.readAllBytes()).thenReturn(fileBytes);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(objectResponse);

        // when
        var result = minioService.downloadObject(fileMeta);

        // then
        assertThat(result).isEqualTo(fileBytes);
        verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
    }

    @Test
    public void removeObject_shouldCallMinioClient() throws Exception {
        // given
        var fileMeta = mock(FileMeta.class);

        when(fileMeta.getBucket()).thenReturn("test-bucket");
        when(fileMeta.getObjectPath()).thenReturn("test-object");

        // when
        minioService.removeObject(fileMeta);

        // then
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }
}