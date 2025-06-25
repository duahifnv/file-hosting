package org.duahifnv.filehosting.repository;

import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.SharedMeta;
import org.duahifnv.filehosting.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SharedMetaRepository extends JpaRepository<SharedMeta, UUID> {

    @Query("SELECT sf FROM SharedMeta sf " +
            "JOIN FileMeta fm ON sf.metadata = fm " +
            "WHERE fm = :fileMeta")
    Page<SharedMeta> findSharedMetasByFileMeta(FileMeta fileMeta, Pageable pageable);

    @Query("SELECT fm FROM SharedMeta sf " +
            "LEFT JOIN FileMeta fm ON sf.metadata = fm " +
            "JOIN User u ON fm.user.username = :#{#user.username}")
    Page<FileMeta> findFileMetasByUser(User user, Pageable pageable);
}
