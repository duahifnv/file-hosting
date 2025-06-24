package org.duahifnv.filehosting.controller;

import lombok.RequiredArgsConstructor;
import org.duahifnv.exceptions.ResourceNotFoundException;
import org.duahifnv.filehosting.dto.FileMetaDto;
import org.duahifnv.filehosting.mapper.FileMetaMapper;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.FileMetaService;
import org.duahifnv.filehosting.service.FileService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final FileMetaService metaService;
    private final FileMetaMapper metaMapper;

    @GetMapping("/api/file-metas")
    @ResponseStatus(HttpStatus.OK)
    public List<FileMetaDto> getAllFileMetas(@RequestParam(required = false) String contentType,
                                             @AuthenticationPrincipal User user,
                                             Pageable pageable) {
        if (contentType != null) {
            return metaMapper.toDtos(
                    metaService.findAllByContentTypeAndUser(contentType, user, pageable)
            );
        }
        return metaMapper.toDtos(metaService.findAllByUser(user, pageable));
    }

    @GetMapping("/api/file-metas/{metaId}")
    @ResponseStatus(HttpStatus.OK)
    public FileMetaDto getFileMeta(@PathVariable UUID metaId,
                                @AuthenticationPrincipal User user) {
        return metaMapper.toDto(metaService.findById(metaId, user)
                .orElseThrow(ResourceNotFoundException::new));
    }

    @GetMapping("/api/files/{metaId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getFileById(@PathVariable UUID metaId, @AuthenticationPrincipal User user) {
        try {
            return fileService.downloadFile(metaId, user)
                    .map(data -> ResponseEntity.ok()
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
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file, @AuthenticationPrincipal UserDetails user) {
        try {
            UUID fileId = fileService.uploadFile(file, (User) user);
            return ResponseEntity
                    .created(URI.create("/api/files/" + fileId))
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при добавлении нового файла: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/files/{metaId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFile(@PathVariable UUID metaId, @AuthenticationPrincipal User user) {
        try {
            if (!fileService.removeFile(metaId, user)) {
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
