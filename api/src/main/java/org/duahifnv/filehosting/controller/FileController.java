package org.duahifnv.filehosting.controller;

import lombok.RequiredArgsConstructor;
import org.duahifnv.exceptions.ResourceNotFoundException;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/api/files/{id}")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getFileById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        try {
            return fileService.downloadFile(id, user)
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
    public void uploadFile(@RequestPart("file") MultipartFile file, @AuthenticationPrincipal User user) {
        try {
            fileService.uploadFile(file, user);
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
