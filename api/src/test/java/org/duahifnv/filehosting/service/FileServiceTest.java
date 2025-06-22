package org.duahifnv.filehosting.service;

import org.duahifnv.filehosting.model.CryptoData;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    private static final byte[] testBytes = new byte[] {10, -23, 12};
    private static final UUID fileId = UUID.fromString("79f3e08b-6d7c-44d4-bb7f-d502eab194de");

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileMetaService metaService;
    @Mock
    private FileCryptoService cryptoService;
    @Mock
    private MinioService minioService;

    @Test
    void downloadFile_shouldReturnFileBytes_withExistingFile() throws Exception {
        // given
        var user = mock(User.class);

        var fileMeta = mock(FileMeta.class);

        when(metaService.findById(fileId, user)).thenReturn(Optional.of(fileMeta));
        when(minioService.downloadObject(fileMeta)).thenReturn(testBytes);

        var decryptedBytes = testBytes;
        when(cryptoService.decryptData(any(CryptoData.class))).thenReturn(decryptedBytes);

        // when
        var result = fileService.downloadFile(fileId, user);

        // then
        verify(metaService).findById(fileId, user);
        verify(minioService).downloadObject(fileMeta);
        verify(cryptoService).decryptData(any(CryptoData.class));
        assertThat(result).isPresent().hasValue(decryptedBytes);
    }

    @Test
    void downloadFile_shouldThrowException_withNonExistingFile() throws Exception {
        // given
        var user = mock(User.class);

        when(metaService.findById(fileId, user)).thenReturn(Optional.empty());

        // when
        var result = fileService.downloadFile(fileId, user);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void uploadFile_shouldUploadToMinioAndSaveMeta() throws Exception {
        // given
        var file = mock(MultipartFile.class);
        var fileStream = mock(InputStream.class);

        when(file.getInputStream()).thenReturn(fileStream);

        var cryptoData = mock(CryptoData.class);
        when(cryptoData.bytes()).thenReturn(testBytes);

        when(cryptoService.encryptStream(fileStream)).thenReturn(cryptoData);

        var user = mock(User.class);

        // when
        fileService.uploadFile(file, user);

        // then
        verify(metaService, times(1)).save(any(FileMeta.class));
        verify(minioService, times(1)).uploadObject(any(FileMeta.class), eq(testBytes));
    }

    @Test
    void removeFile_shouldRemoveMetaAndRemoveMinioObject_withExistingFile() throws Exception {
        // given
        var user = mock(User.class);
        var fileMeta = mock(FileMeta.class);
        when(metaService.findById(fileId, user)).thenReturn(Optional.of(fileMeta));

        // when
        boolean isRemoved = fileService.removeFile(fileId, user);

        // then
        assertThat(isRemoved).isTrue();
        verify(minioService, times(1)).removeObject(fileMeta);
        verify(metaService, times(1)).remove(fileMeta);
    }

    @Test
    void removeFile_shouldDoNothing_withNonExistingFile() throws Exception {
        // given
        var user = mock(User.class);
        when(metaService.findById(fileId, user)).thenReturn(Optional.empty());

        // when
        boolean isRemoved = fileService.removeFile(fileId, user);

        // then
        assertThat(isRemoved).isFalse();
        verify(minioService, never()).removeObject(any(FileMeta.class));
        verify(metaService, never()).remove(any(FileMeta.class));
    }
}