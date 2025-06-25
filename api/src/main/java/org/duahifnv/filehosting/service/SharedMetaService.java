package org.duahifnv.filehosting.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.dto.SharedMetaNewDto;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.SharedMeta;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.repository.SharedMetaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharedMetaService {
    private final SharedMetaRepository repository;
    private final UserService userService;

    public List<FileMeta> findFileMetasByUser(@NotNull User user, Pageable pageable) {
        return repository.findFileMetasByUser(user, pageable).stream().toList();
    }

    public Optional<SharedMeta> findById(UUID sharedId) {
        return repository.findById(sharedId);
    }

    public List<SharedMeta> findSharedMetas(FileMeta fileMeta, Pageable pageable) {
        return repository.findSharedMetasByFileMeta(fileMeta, pageable).stream().toList();
    }

    @Transactional
    public SharedMeta createSharedMeta(SharedMetaNewDto sharedMetaNewDto, FileMeta fileMeta) {
        List<User> sharedUsers = userService.findUsersByEmails(sharedMetaNewDto.sharedUsersEmails());
        try {
            Duration sharingDuration = Duration.parse(sharedMetaNewDto.sharingLifetime());
            if (sharingDuration.compareTo(Duration.ofMinutes(1)) < 0) {
                throw new Exception("Длина жизни общего файла должна быть >= 1 min");
            }
            return repository.save(SharedMeta.of(fileMeta, sharedUsers, sharingDuration));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный формат времени");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional
    public void removeSharedMetas(FileMeta fileMeta) {
        List<SharedMeta> sharedMetas = findSharedMetas(fileMeta, Pageable.unpaged());
        repository.deleteAll(sharedMetas);
    }
}
