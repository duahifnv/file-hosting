package org.duahifnv.filehosting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duahifnv.exceptions.ResourceNotFoundException;
import org.duahifnv.filehosting.dto.SharedMetaDto;
import org.duahifnv.filehosting.dto.SharedMetaNewDto;
import org.duahifnv.filehosting.mapper.SharedMetaMapper;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.SharedMeta;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.FileMetaService;
import org.duahifnv.filehosting.service.SharedMetaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Управление общим доступом к файлам", description = "API для управления общим доступом к файлам")
@SecurityRequirement(name = "JWT аутентификация")
public class SharedController {
    private final SharedMetaService sharedMetaService;
    private final FileMetaService fileMetaService;

    private final SharedMetaMapper sharedMetaMapper;

    @PutMapping("/api/files/sharing/{fileId}")
    @Operation(summary = "Предоставить общий доступ к файлу", description = "Создает общий доступ к файлу для указанных пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Общий доступ успешно создан",
                    content = @Content(schema = @Schema(implementation = SharedMetaDto.class))),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public SharedMetaDto addSharedFile(
            @Parameter(description = "Идентификатор файла", required = true) @PathVariable UUID fileId,
            @Valid @RequestBody SharedMetaNewDto sharedMetaNewDto,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        FileMeta fileMeta = fileMetaService.findById(fileId, user)
                .orElseThrow(ResourceNotFoundException::new);

        SharedMeta sharedMeta = sharedMetaService.createSharedMeta(sharedMetaNewDto, fileMeta);
        return sharedMetaMapper.toDto(sharedMeta);
    }

    @DeleteMapping("/api/files/sharing/{fileId}")
    @Operation(summary = "Удалить весь общий доступ к файлу", description = "Удаляет все общие доступы к указанному файлу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Общий доступ успешно удален"),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public void removeAllSharesFromFile(
            @Parameter(description = "Идентификатор файла", required = true) @PathVariable UUID fileId,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        FileMeta fileMeta = fileMetaService.findById(fileId, user)
                .orElseThrow(ResourceNotFoundException::new);

        sharedMetaService.removeSharedMetas(fileMeta);
    }
}
