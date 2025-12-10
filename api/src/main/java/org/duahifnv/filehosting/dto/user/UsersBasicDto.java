package org.duahifnv.filehosting.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO со списком пользователей")
public record UsersBasicDto(
        @Schema(description = "Список пользователей")
        List<UserBasicDto> users) {
}
