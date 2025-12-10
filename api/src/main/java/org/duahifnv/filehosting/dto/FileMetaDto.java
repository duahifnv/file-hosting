package org.duahifnv.filehosting.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "DTO с метаданными файла")
public record FileMetaDto(
        @Schema(description = "Идентификатор файла", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Имя пользователя владельца файла", example = "john_doe")
        String username,

        @Schema(description = "Оригинальное имя файла", example = "document.pdf")
        String originalName,

        @Schema(description = "MIME тип файла", example = "application/pdf")
        String contentType,

        @Schema(description = "Размер файла в байтах", example = "1048576")
        Long originalSize,

        @Schema(description = "Дата и время загрузки файла", example = "2024-01-01T12:00:00+00:00")
        OffsetDateTime createdAt,

        @Schema(description = "Дата и время истечения срока хранения файла", example = "2024-01-31T12:00:00+00:00")
        OffsetDateTime expiresAt) {
}
