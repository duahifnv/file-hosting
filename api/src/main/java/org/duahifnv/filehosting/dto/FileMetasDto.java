package org.duahifnv.filehosting.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO со списком метаданных файлов")
public record FileMetasDto(
        @Schema(description = "Список метаданных файлов")
        List<FileMetaDto> fileMetas) {
}
