package org.duahifnv.filehosting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.duahifnv.exceptions.ResourceNotFoundException;
import org.duahifnv.filehosting.dto.FileMetaDto;
import org.duahifnv.filehosting.dto.FileMetasDto;
import org.duahifnv.filehosting.dto.pageable.FilePageableDto;
import org.duahifnv.filehosting.mapper.FileMetaMapper;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.FileMetaService;
import org.duahifnv.filehosting.service.FileService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Управление файлами", description = "API для загрузки, скачивания и управления файлами")
@SecurityRequirement(name = "JWT аутентификация")
public class FileController {
    private final FileService fileService;
    private final FileMetaService metaService;
    private final FileMetaMapper metaMapper;

    @GetMapping("/api/file-metas")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить список метаданных файлов", description = "Возвращает список метаданных файлов пользователя с возможностью фильтрации по типу контента и общим файлам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список метаданных успешно получен",
                    content = @Content(schema = @Schema(implementation = FileMetasDto.class))),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public FileMetasDto getAllFileMetas(
            @Parameter(description = "Тип контента для фильтрации") @RequestParam(required = false) String contentType,
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Получить только общие файлы") @RequestParam(required = false) boolean shared,
            @Parameter(description = "Параметры пагинации") FilePageableDto page) {
        List<FileMeta> fileMetas;
        Pageable pageable = page.pageable();

        if (shared)
            fileMetas = metaService.findAllShared(user, pageable);
        else if (contentType != null)
            fileMetas = metaService.findAllByContentTypeAndUser(contentType, user, pageable);
        else
            fileMetas = metaService.findAllByUser(user, pageable);
        return new FileMetasDto(metaMapper.toDtos(fileMetas));
    }

    @GetMapping("/api/file-metas/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить метаданные файла", description = "Возвращает метаданные конкретного файла")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Метаданные успешно получены",
                    content = @Content(schema = @Schema(implementation = FileMetaDto.class))),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "410", description = "Срок действия файла истек")
    })
    public FileMetaDto getFileMeta(
            @Parameter(description = "Идентификатор файла", required = true) @PathVariable UUID fileId,
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Получить из общих файлов") @RequestParam(required = false) boolean shared) {
        var fileMeta = shared ?
                metaService.findByIdShared(fileId, user) :
                metaService.findById(fileId, user);

        return metaMapper.toDto(fileMeta.orElseThrow(ResourceNotFoundException::new));
    }

    @GetMapping("/api/files/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Скачать файл", description = "Скачивает файл по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно скачан",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<?> getFileById(
            @Parameter(description = "Идентификатор файла", required = true) @PathVariable UUID fileId,
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Скачать из общих файлов") @RequestParam(required = false) boolean shared) {
        try {
            var fileData = shared ?
                    fileService.downloadSharedFile(fileId, user) :
                    fileService.downloadFile(fileId, user);

            return fileData.map(data -> ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + data.metaData().getOriginalName() + "\"")
                                        .header(HttpHeaders.CONTENT_TYPE, data.metaData().getContentType())
                                        .body(data.bytes()))
                    .orElseThrow(ResourceNotFoundException::new);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    @PostMapping(path = "/api/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Загрузить файл", description = "Загружает новый файл в систему")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Файл успешно загружен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "Файл для загрузки", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("file") MultipartFile file,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            UUID fileId = fileService.uploadFile(file, user);
            return ResponseEntity
                    .created(URI.create("/api/files/" + fileId))
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при добавлении нового файла: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/files/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить файл", description = "Удаляет файл по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно удален"),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public void removeFile(
            @Parameter(description = "Идентификатор файла", required = true) @PathVariable UUID fileId,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        try {
            if (!fileService.removeFile(fileId, user)) {
                throw new ResourceNotFoundException();
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при удалении файла: " + e.getMessage());
        }
    }
}
