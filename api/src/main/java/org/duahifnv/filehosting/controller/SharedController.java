package org.duahifnv.filehosting.controller;

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
public class SharedController {
    private final SharedMetaService sharedMetaService;
    private final FileMetaService fileMetaService;

    private final SharedMetaMapper sharedMetaMapper;

    @PutMapping("/api/files/sharing/{fileId}")
    public SharedMetaDto addSharedFile(@PathVariable UUID fileId, @RequestBody SharedMetaNewDto sharedMetaNewDto,
                                       @AuthenticationPrincipal User user) {
        FileMeta fileMeta = fileMetaService.findById(fileId, user)
                .orElseThrow(ResourceNotFoundException::new);

        SharedMeta sharedMeta = sharedMetaService.createSharedMeta(sharedMetaNewDto, fileMeta);
        return sharedMetaMapper.toDto(sharedMeta);
    }

    @DeleteMapping("/api/files/sharing/{fileId}")
    public void removeAllSharesFromFile(@PathVariable UUID fileId, @AuthenticationPrincipal User user) {
        FileMeta fileMeta = fileMetaService.findById(fileId, user)
                .orElseThrow(ResourceNotFoundException::new);

        sharedMetaService.removeSharedMetas(fileMeta);
    }
}
