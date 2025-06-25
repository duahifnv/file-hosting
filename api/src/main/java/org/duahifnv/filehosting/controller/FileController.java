package org.duahifnv.filehosting.controller;

import lombok.RequiredArgsConstructor;
import org.duahifnv.exceptions.ResourceNotFoundException;
import org.duahifnv.filehosting.dto.FileMetaDto;
import org.duahifnv.filehosting.dto.FileMetasDto;
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
public class FileController {
    private final FileService fileService;
    private final FileMetaService metaService;
    private final FileMetaMapper metaMapper;

    @GetMapping("/api/file-metas")
    @ResponseStatus(HttpStatus.OK)
    public FileMetasDto getAllFileMetas(@RequestParam(required = false) String contentType,
                                                          @AuthenticationPrincipal User user,
                                                          @RequestParam(required = false) boolean shared,
                                                          Pageable pageable) {
        List<FileMeta> fileMetas;
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
    public FileMetaDto getFileMeta(@PathVariable UUID fileId, @AuthenticationPrincipal User user,
                                   @RequestParam(required = false) boolean shared) {
        var fileMeta = shared ?
                metaService.findByIdShared(fileId, user) :
                metaService.findById(fileId, user);

        return metaMapper.toDto(fileMeta.orElseThrow(ResourceNotFoundException::new));
    }

    @GetMapping("/api/files/{fileId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getFileById(@PathVariable UUID fileId, @AuthenticationPrincipal User user,
                                              @RequestParam(required = false) boolean shared) {
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
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file, @AuthenticationPrincipal User user) {
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
    public void removeFile(@PathVariable UUID fileId, @AuthenticationPrincipal User user) {
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
