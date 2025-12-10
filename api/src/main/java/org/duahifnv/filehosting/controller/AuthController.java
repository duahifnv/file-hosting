package org.duahifnv.filehosting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.dto.user.AuthDto;
import org.duahifnv.filehosting.dto.user.RegisterDto;
import org.duahifnv.filehosting.service.AuthService;
import org.duahifnv.jwtauthstarter.jwt.JwtDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Аутентификация и регистрация", description = "API для аутентификации и регистрации пользователей")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/api/auth")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Аутентификация пользователя", description = "Выполняет аутентификацию пользователя и возвращает JWT токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аутентификация успешна",
                    content = @Content(schema = @Schema(implementation = JwtDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public JwtDto authenticate(
            @Parameter(description = "Данные для аутентификации", required = true)
            @RequestBody @Valid AuthDto authDto) {
        return authService.authenticateUser(authDto);
    }

    @PostMapping("/api/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация нового пользователя", description = "Регистрирует нового пользователя и возвращает JWT токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = JwtDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public JwtDto register(
            @Parameter(description = "Данные для регистрации", required = true)
            @RequestBody @Valid RegisterDto registerDto) {
        return authService.registerNewUser(registerDto);
    }
}
