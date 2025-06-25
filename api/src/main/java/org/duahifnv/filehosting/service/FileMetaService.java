package org.duahifnv.filehosting.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.exception.ResourceExpiredException;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.SharedMeta;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.repository.FileMetaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileMetaService {
    private final FileMetaRepository repository;
    private final SharedMetaService sharedMetaService;

    protected static final Logger log = LoggerFactory.getLogger(FileMetaService.class);

    public Optional<FileMeta> findById(UUID id, @NotNull User user) {
        return repository.findById(id)
                .map(f -> f.getUser().equals(user) ? f : null);
    }

    public Optional<FileMeta> findByIdShared(UUID id, @NotNull User requestingUser) {
        Optional<SharedMeta> sharedOptional = sharedMetaService.findById(id);
        if (sharedOptional.isEmpty()) {
            return Optional.empty();
        }

        SharedMeta sharedMeta = sharedOptional.get();
        // todo: Перевести время в нужный часовой пояс
        if (sharedMeta.isExpired()) {
            throw new ResourceExpiredException(sharedMeta.getExpiresAt());
        }

        return switch (sharedMeta.getShareMode()) {
            case LINKED -> Optional.of(sharedMeta.getMetadata());
            case SELECTIVE -> sharedMeta.getSharedUsers().contains(requestingUser) ? Optional.of(sharedMeta.getMetadata()) : Optional.empty();
        };
    }

    public List<FileMeta> findAllByUser(@NotNull User user, Pageable pageable) {
        return repository.findByUser(user, pageable)
                .stream().toList();
    }

    public List<FileMeta> findAllShared(@NotNull User requestingUser, Pageable pageable) {
        return sharedMetaService.findFileMetasByUser(requestingUser, pageable);
    }

    public List<FileMeta> findAllByContentTypeAndUser(String contentType, @NotNull User user, Pageable pageable) {
        return repository.findAllByContentTypeAndUser(contentType, user, pageable)
                .stream().toList();
    }

    @Transactional
    public FileMeta save(@NotNull FileMeta fileMeta) {
        FileMeta savedFile = repository.save(fileMeta);
        log.debug("Meta: Метаданные файла [{}] добавлены", fileMeta.getOriginalName());
        return savedFile;
    }

    @Transactional
    public void remove(UUID id, @NotNull User user) {
        findById(id, user).ifPresent(this::remove);
    }

    @Transactional
    public void remove(FileMeta meta) {
        sharedMetaService.removeSharedMetas(meta);
        repository.delete(meta);
        log.debug("Meta: Метаданные файла [{}] удалены", meta.getOriginalName());
    }
}
