package org.duahifnv.filehosting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duahifnv.exceptions.UserNotFoundException;
import org.duahifnv.filehosting.dto.user.UserBasicDto;
import org.duahifnv.filehosting.dto.user.UserFormDto;
import org.duahifnv.filehosting.dto.user.UsersBasicDto;
import org.duahifnv.filehosting.mapper.UserMapper;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Управление пользователями", description = "API для управления пользователями")
@SecurityRequirement(name = "JWT аутентификация")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/api/users")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить список пользователей", description = "Возвращает список всех пользователей с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен",
                    content = @Content(schema = @Schema(implementation = UsersBasicDto.class))),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public UsersBasicDto getAllUsers(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return new UsersBasicDto(
                userMapper.toBasicDtos(userService.findAll(pageable))
        );
    }

    @GetMapping("/api/user")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить пользователя по email", description = "Возвращает базовую информацию о пользователе по его email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно найден",
                    content = @Content(schema = @Schema(implementation = UserBasicDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public UserBasicDto getUser(
            @Parameter(description = "Email пользователя", required = true, example = "user1@mail.ru") @RequestParam String email) {
        return userMapper.toBasicDto(userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email)));
    }

    @GetMapping("/api/user/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить информацию о текущем пользователе", description = "Возвращает полную информацию о текущем аутентифицированном пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена",
                    content = @Content(schema = @Schema(implementation = UserFormDto.class))),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public UserFormDto getUserForm(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return userMapper.toFormDto(user);
    }

    @PutMapping("/api/user/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить информацию о текущем пользователе", description = "Обновляет информацию о текущем аутентифицированном пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public void updateUser(
            @Parameter(description = "Данные для обновления пользователя", required = true)
            @Valid @RequestBody UserFormDto updatedUser,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        userMapper.updateUser(user, updatedUser);
        userService.update(user);
    }
}
