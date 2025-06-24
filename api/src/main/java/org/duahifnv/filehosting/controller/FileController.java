package org.duahifnv.filehosting.controller;

import lombok.RequiredArgsConstructor;
import org.duahifnv.exceptions.ResourceNotFoundException;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.FileService;
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
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/api/files/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getFileById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        try {
            return fileService.downloadFile(id, user)
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

    @DeleteMapping("/api/files/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeFile(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        try {
            if (!fileService.removeFile(id, user)) {
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
