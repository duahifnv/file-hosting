package org.duahifnv.filehosting.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SharedMetaDto(UUID sharedId, OffsetDateTime sharedAt, OffsetDateTime expiresAt) {
}
