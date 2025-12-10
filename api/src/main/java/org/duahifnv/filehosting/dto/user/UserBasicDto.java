package org.duahifnv.filehosting.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO с базовой информацией о пользователе")
public record UserBasicDto(
        @Schema(description = "Email пользователя", example = "user@example.com")
        String email,

        @Schema(description = "Имя пользователя", example = "Иван")
        String firstname,

        @Schema(description = "Фамилия пользователя", example = "Иванов")
        String lastname) {}