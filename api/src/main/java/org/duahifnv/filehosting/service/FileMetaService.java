package org.duahifnv.filehosting.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.repository.FileMetaRepository;
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

    public Optional<FileMeta> findById(UUID id, @NotNull User user) {
        return repository.findById(id)
                .map(f -> f.getUser().equals(user) ? f : null);
    }

    public List<FileMeta> findAllByUser(@NotNull User user, Pageable pageable) {
        return repository.findByUser(user, pageable)
                .stream().toList();
    }

    public List<FileMeta> findAllByContentTypeAndUser(String contentType, @NotNull User user, Pageable pageable) {
        return repository.findAllByContentTypeAndUser(contentType, user, pageable)
                .stream().toList();
    }

    @Transactional
    public FileMeta save(@NotNull FileMeta fileMeta) {
        return repository.save(fileMeta);
    }

    @Transactional
    public void remove(UUID id, @NotNull User user) {
        findById(id, user).ifPresent(repository::delete);
    }

    @Transactional
    public void remove(FileMeta meta) {
        repository.delete(meta);
    }
}
