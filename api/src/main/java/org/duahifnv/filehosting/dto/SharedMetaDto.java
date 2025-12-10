package org.duahifnv.filehosting.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "DTO с информацией об общем доступе к файлу")
public record SharedMetaDto(
        @Schema(description = "Идентификатор общего доступа", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID sharedId,

        @Schema(description = "Дата и время создания общего доступа", example = "2024-01-01T12:00:00+00:00")
        OffsetDateTime createdAt,

        @Schema(description = "Дата и время истечения общего доступа", example = "2024-01-31T12:00:00+00:00")
        OffsetDateTime expiresAt) {
}
