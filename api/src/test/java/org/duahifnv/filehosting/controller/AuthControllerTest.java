package org.duahifnv.filehosting.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.duahifnv.filehosting.dto.AuthDto;
import org.duahifnv.filehosting.dto.UserDto;
import org.duahifnv.filehosting.filter.AuthFilter;
import org.duahifnv.filehosting.service.AuthService;
import org.duahifnv.jwtauthstarter.jwt.JwtDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Тестирование контроллера аутентификации")
class AuthControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Mock
    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AuthFilter authFilter;

    @Test
    @DisplayName("Должен возвращать токен при успешной аутентификации")
    void authenticate_shouldReturnJwt() throws Exception {
        // given
        var jwtDto = new JwtDto("token");
        when(authService.authenticateUser(any(AuthDto.class)))
                .thenReturn(jwtDto);
        var authDto = new AuthDto("john", "password");

        // when
        mvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));

        // then
        verify(authService).authenticateUser(any(AuthDto.class));
    }

    @Test
    void authenticate_whenUsernameTooShort_thenReturns400() throws Exception {
        // given
        var authDto = new AuthDto("3ch", "password");

        // then
        mvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticate_whenPasswordTooShort_thenReturns400() throws Exception {
        // given
        var authDto = new AuthDto("validUser", "short");

        // then
        mvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnNewToken_withValidUserDto() throws Exception {
        // given
        var userDto = new UserDto("john", "password");
        var jwtDto = new JwtDto("token");

        when(authService.registerNewUser(any(UserDto.class)))
                .thenReturn(jwtDto);
        // then
        mvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token"));

        // then
        verify(authService).registerNewUser(any(UserDto.class));
    }
}