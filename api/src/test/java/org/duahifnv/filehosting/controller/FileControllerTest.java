package org.duahifnv.filehosting.controller;

import org.duahifnv.filehosting.filter.AuthFilter;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.FileService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void getFileById_returnsFileById_withExistingFile() throws Exception {
        // given
        var fileId = UUID.fromString("f7624426-7c58-4826-b63b-8b34737807ee");
        var fileBytes = new byte[] {10, -30, -35};

        when(fileService.downloadFile(eq(fileId), any())).thenReturn(Optional.of(fileBytes));

        // when
        mvc.perform(get("/api/files/" + fileId))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileBytes));

        // then
        verify(fileService).downloadFile(eq(fileId), any());
    }

    @Test
    void getFileById_returns404_withNonExistingFile() throws Exception {
        // given
        var fileId = UUID.fromString("f7624426-7c58-4826-b63b-8b34737807ee");
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
        doNothing().when(fileService).uploadFile(eq(file), any());

        // when
        mvc.perform(multipart("/api/files")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

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
        doThrow(Exception.class).when(fileService).uploadFile(eq(file), any());

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
        var fileId = UUID.fromString("f7624426-7c58-4826-b63b-8b34737807ee");
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
        var fileId = UUID.fromString("f7624426-7c58-4826-b63b-8b34737807ee");
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
        var fileId = UUID.fromString("f7624426-7c58-4826-b63b-8b34737807ee");
        doThrow(Exception.class).when(fileService).removeFile(eq(fileId), any());

        // when
        mvc.perform(delete("/api/files/" + fileId))
                .andExpect(status().isInternalServerError());

        // then
        verify(fileService).removeFile(eq(fileId), any());
    }
}
