package org.duahifnv.filehosting.model.listener;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.model.SharedMeta;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class SharedMetaEntityListener {
    private final Clock clock;

    @PrePersist
    public void setSharedLifetime(SharedMeta sharedMeta) {
        sharedMeta.setCreatedAt(OffsetDateTime.now(clock));
        Duration sharedDuration = sharedMeta.getSharedDuration();

        if (sharedDuration != null) {
            sharedMeta.setExpiresAt(sharedMeta.getCreatedAt().plus(sharedDuration));
        }
    }

    @PostLoad
    public void validateExpiration(SharedMeta sharedMeta) {
        if (sharedMeta.isExpired()) return;

        OffsetDateTime expiresAt = sharedMeta.getExpiresAt();
        if (expiresAt != null && expiresAt.toInstant().isBefore(clock.instant())) {
            sharedMeta.setExpired(true);
        }
    }
}
