package org.duahifnv.filehosting.controller;

import org.duahifnv.filehosting.dto.FileData;
import org.duahifnv.filehosting.filter.AuthFilter;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = FileController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AuthFilter.class})
)
@AutoConfigureMockMvc(addFilters = false)
class FileControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private FileService fileService;

    private final UUID fileId = UUID.fromString("f7624426-7c58-4826-b63b-8b34737807ee");

    @Test
    void getFileById_returnsFileById_withExistingFile() throws Exception {
        // given
        var fileBytes = new byte[] {10, -30, -35};

        var fileMeta = mock(FileMeta.class);
        when(fileMeta.getContentType()).thenReturn("text/plain");
        when(fileMeta.getOriginalName()).thenReturn("file_name");

        var fileData = new FileData(fileMeta, fileBytes);
        when(fileService.downloadFile(eq(fileId), any())).thenReturn(Optional.of(fileData));

        // when
        mvc.perform(get("/api/files/" + fileId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"))
                .andExpect(content().bytes(fileBytes));

        // then
        verify(fileService).downloadFile(eq(fileId), any());
    }

    @Test
    void getFileById_returns404_withNonExistingFile() throws Exception {
        // given
        when(fileService.downloadFile(eq(fileId), any())).thenReturn(Optional.empty());

        // when
        mvc.perform(get("/api/files/" + fileId))
                .andExpect(status().isNotFound());

        // then
        verify(fileService).downloadFile(eq(fileId), any());
    }

    @Test
    void uploadNewFile_shouldUploadFile() throws Exception {
        // given
        var fileData = "file-data";

        var file = new MockMultipartFile(
                "file", // parameter name in the controller, e.g., @RequestParam("file")
                "test.txt", // original filename
                "text/plain", // content type
                fileData.getBytes() // content as byte array
        );
        when(fileService.uploadFile(eq(file), any())).thenReturn(fileId);

        // when
        mvc.perform(multipart("/api/files")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string("location",
                        containsString("/api/files/" + fileId))
                );

        // then
        verify(fileService).uploadFile(eq(file), any());
    }

    @Test
    void uploadFile_shouldReturn500_ifServerError() throws Exception {
        // given
        var fileData = "file-data";
        var file = new MockMultipartFile(
                "file", // parameter name in the controller, e.g., @RequestParam("file")
                "test.txt", // original filename
                "text/plain", // content type
                fileData.getBytes() // content as byte array
        );
        when(fileService.uploadFile(eq(file), any())).thenThrow(Exception.class);

        // when
        mvc.perform(multipart("/api/files")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());

        // then
        verify(fileService).uploadFile(eq(file), any());
    }

    @Test
    void removeFile_shouldRemoveFile_withExistingFile() throws Exception {
        // given
        when(fileService.removeFile(eq(fileId), any())).thenReturn(true);

        // when
        mvc.perform(delete("/api/files/" + fileId))
                .andExpect(status().isOk());

        // then
        verify(fileService).removeFile(eq(fileId), any());
    }

    @Test
    void removeFile_shouldThrow404_withNonExistingFile() throws Exception {
        // given
        when(fileService.removeFile(eq(fileId), any())).thenReturn(false);

        // when
        mvc.perform(delete("/api/files/" + fileId))
                .andExpect(status().isNotFound());

        // then
        verify(fileService).removeFile(eq(fileId), any());
    }

    @Test
    void removeFile_shouldThrow500_ifServerError() throws Exception {
        // given
        doThrow(Exception.class).when(fileService).removeFile(eq(fileId), any());

        // when
        mvc.perform(delete("/api/files/" + fileId))
                .andExpect(status().isInternalServerError());

        // then
        verify(fileService).removeFile(eq(fileId), any());
    }
}
