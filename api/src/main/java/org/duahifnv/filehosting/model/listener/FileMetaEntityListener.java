package org.duahifnv.filehosting.model.listener;

import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.model.FileMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
@RequiredArgsConstructor
public class FileMetaEntityListener {
    private final Clock clock;
    // todo: Добавить валидацию
    @Value("${file.lifetime}")
    public Duration fileLifetime;

    // todo: @PostLoad для валидации на истекший файл
    @PrePersist
    public void setFileMeta(FileMeta fileMeta) {
        OffsetDateTime dateTime = OffsetDateTime.ofInstant(
                Instant.now(clock), ZoneId.of("UTC")
        );
        if (fileMeta.getCreatedAt() == null) {
            fileMeta.setCreatedAt(dateTime);
        }
        if (fileMeta.getExpiresAt() == null) {
            fileMeta.setExpiresAt(dateTime.plus(fileLifetime));
        }
    }
}
