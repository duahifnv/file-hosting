package org.duahifnv.filehosting.service;

import org.duahifnv.exceptions.ResourceNotFoundException;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.repository.FileMetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileMetaServiceTest {

    @Mock
    private FileMetaRepository fileMetaRepository;

    @InjectMocks
    private FileMetaService fileMetaService;

    private User testUser;
    private User otherUser;
    private FileMeta testFileMeta;
    private FileMeta otherUserFileMeta;
    private UUID testFileId;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");

        testFileId = UUID.randomUUID();

        otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setUsername("otheruser");

        testFileMeta = mock(FileMeta.class);
        otherUserFileMeta = mock(FileMeta.class);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void findById_WhenFileExistsAndBelongsToUser_ShouldReturnFile() {
        when(fileMetaRepository.findById(testFileId)).thenReturn(Optional.of(testFileMeta));
        when(testFileMeta.getUser()).thenReturn(testUser);

        var result = fileMetaService.findById(testFileId, testUser);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testFileMeta);
        verify(fileMetaRepository).findById(testFileId);
    }

    @Test
    void findById_WhenFileExistsButBelongsToOtherUser_ShouldReturnEmpty() {
        when(fileMetaRepository.findById(testFileId)).thenReturn(Optional.of(otherUserFileMeta));
        when(otherUserFileMeta.getUser()).thenReturn(otherUser);

        var result = fileMetaService.findById(testFileId, testUser);

        assertThat(result).isEmpty();
        verify(fileMetaRepository).findById(testFileId);
    }

    @Test
    void findById_WhenFileDoesNotExist_ShouldReturnEmpty() {
        when(fileMetaRepository.findById(testFileId)).thenReturn(Optional.empty());

        var result = fileMetaService.findById(testFileId, testUser);

        assertThat(result).isEmpty();
        verify(fileMetaRepository).findById(testFileId);
    }

    @Test
    void findAllByUser_ShouldReturnUserFiles() {
        var page = new PageImpl<>(List.of(testFileMeta));
        when(fileMetaRepository.findByUser(testUser, pageable)).thenReturn(page);

        var result = fileMetaService.findAllByUser(testUser, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testFileMeta);
        verify(fileMetaRepository).findByUser(testUser, pageable);
    }

    @Test
    void findAllByUser_WhenNoFiles_ShouldReturnEmptyList() {
        Page<FileMeta> page = new PageImpl<>(List.of());
        when(fileMetaRepository.findByUser(testUser, pageable)).thenReturn(page);

        var result = fileMetaService.findAllByUser(testUser, pageable);

        assertThat(result).isEmpty();
        verify(fileMetaRepository).findByUser(testUser, pageable);
    }

    @Test
    void findAllByContentTypeAndUser_ShouldReturnFilteredFiles() {
        var page = new PageImpl<>(List.of(testFileMeta));
        when(fileMetaRepository.findAllByContentTypeAndUser("text/plain", testUser, pageable)).thenReturn(page);

        var result = fileMetaService.findAllByContentTypeAndUser("text/plain", testUser, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testFileMeta);
        verify(fileMetaRepository).findAllByContentTypeAndUser("text/plain", testUser, pageable);
    }

    @Test
    void findAllByContentTypeAndUser_WhenNoMatchingFiles_ShouldReturnEmptyList() {
        Page<FileMeta> page = new PageImpl<>(List.of());
        when(fileMetaRepository.findAllByContentTypeAndUser("image/jpeg", testUser, pageable)).thenReturn(page);

        var result = fileMetaService.findAllByContentTypeAndUser("image/jpeg", testUser, pageable);

        assertThat(result).isEmpty();
        verify(fileMetaRepository).findAllByContentTypeAndUser("image/jpeg", testUser, pageable);
    }

    @Test
    void save_ShouldSaveAndReturnFileMeta() {
        when(fileMetaRepository.save(any(FileMeta.class))).thenReturn(testFileMeta);

        FileMeta result = fileMetaService.save(testFileMeta);

        assertThat(result).isEqualTo(testFileMeta);
        verify(fileMetaRepository).save(testFileMeta);
    }

    @Test
    void remove_WhenFileExistsAndBelongsToUser_ShouldDeleteFile() {
        when(fileMetaRepository.findById(testFileId)).thenReturn(Optional.of(testFileMeta));
        when(testFileMeta.getUser()).thenReturn(testUser);

        doNothing().when(fileMetaRepository).delete(testFileMeta);
        fileMetaService.remove(testFileId, testUser);

        verify(fileMetaRepository).findById(testFileId);
        verify(fileMetaRepository).delete(testFileMeta);
    }

    @Test
    void remove_WhenFileExistsButBelongsToOtherUser_ShouldThrowResourceNotFoundException() {
        when(fileMetaRepository.findById(testFileId)).thenReturn(Optional.of(otherUserFileMeta));
        when(otherUserFileMeta.getUser()).thenReturn(otherUser);

        assertThatThrownBy(() -> fileMetaService.remove(testFileId, testUser))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(fileMetaRepository).findById(testFileId);
        verify(fileMetaRepository, never()).delete(any());
    }

    @Test
    void remove_WhenFileDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(fileMetaRepository.findById(testFileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileMetaService.remove(testFileId, testUser))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(fileMetaRepository).findById(testFileId);
        verify(fileMetaRepository, never()).delete(any());
    }
}