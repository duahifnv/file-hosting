package org.duahifnv.filehosting.service;

import org.duahifnv.filehosting.dto.SharedMetaNewDto;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.SharedMeta;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.repository.SharedMetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SharedMetaServiceTest {
    @InjectMocks
    private SharedMetaService sharedMetaService;

    @Mock
    private SharedMetaRepository repository;

    @Mock
    private UserService userService;

    @Test
    void createSharedMeta_shouldCreateNewSharedMeta() {
        // given
        var usersEmails = List.of("user1@mail.ru", "user2@mail.ru", "user3@mail.ru");
        var sharedMetaNewDto = new SharedMetaNewDto(usersEmails, Duration.ofHours(1).toString());
        var fileMeta = mock(FileMeta.class);

        var sharedUsers = List.of(mock(User.class), mock(User.class), mock(User.class));
        when(userService.findUsersByEmails(usersEmails)).thenReturn(sharedUsers);

        // when
        sharedMetaService.createSharedMeta(sharedMetaNewDto, fileMeta);

        // then
        verify(repository).save(any(SharedMeta.class));
    }
}
