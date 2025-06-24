package org.duahifnv.filehosting.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FileMetaDto(UUID id,
                          String username,
                          String originalName,
                          String contentType,
                          Long originalSize,
                          OffsetDateTime createdAt,
                          OffsetDateTime expiresAt) {
}
