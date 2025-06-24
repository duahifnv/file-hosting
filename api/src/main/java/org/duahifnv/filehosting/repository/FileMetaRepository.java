package org.duahifnv.filehosting.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileMetaRepository extends JpaRepository<FileMeta, UUID> {
    Page<FileMeta> findByUser(@NotNull User user, Pageable pageable);

    Page<FileMeta> findAllByContentTypeAndUser(@Size(max = 100) String contentType, @NotNull User user, Pageable pageable);
}
