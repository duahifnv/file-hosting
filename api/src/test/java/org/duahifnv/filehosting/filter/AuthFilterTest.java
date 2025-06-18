package org.duahifnv.filehosting.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.UserService;
import org.duahifnv.jwtauthstarter.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Фильтра аутентификации")
class AuthFilterTest {

    @InjectMocks
    private AuthFilter authFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext(); // Очищаем контекст перед каждым тестом
    }

    @Test
    @DisplayName("Устанавливает аутентификацию в контекст, если токен аутентификации корректный")
    void doFilterInternal_WhenValidJwt_ShouldSetAuthentication() throws ServletException, IOException {
        // Given
        var jwt = "valid.jwt.token";
        var username = "testUser";
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

        var user = new User();
        user.setUsername(username);
        user.setPassword("password");

        when(jwtService.extractClaim(eq(jwt), any())).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(user);

        // When
        authFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(username);
        assertThat(authentication.getAuthorities()).hasSize(1);
    }

    @Test
    @DisplayName("Пропускает установку аутентификации, если отсутствует заголовок аутентификации")
    void doFilterInternal_WhenNoAuthHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        // Given: Запрос без заголовка Authorization
        request.addHeader("Some-Other-Header", "value");

        // When
        authFilter.doFilterInternal(request, response, filterChain);

        // Then: Фильтр не должен устанавливать аутентификацию и должен продолжить цепочку
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Пропускает установку аутентификации, если токен аутентификации некорректный")
    void doFilterInternal_WhenInvalidAuthHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        // Given: Некорректный заголовок Authorization (без Bearer)
        request.addHeader(HttpHeaders.AUTHORIZATION, "InvalidToken");

        // When
        authFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}