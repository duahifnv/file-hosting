package org.duahifnv.filehosting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Schema(description = "DTO для создания общего доступа к файлу")
public record SharedMetaNewDto(
        @Schema(description = "Список email адресов пользователей, которым предоставляется доступ", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"user1@example.com\", \"user2@example.com\"]")
        @NotNull(message = "Список email адресов не может быть null")
        @NotEmpty(message = "Список email адресов не может быть пустым")
        List<@Email(message = "Некорректный формат email") String> sharedUsersEmails,

        @Schema(description = "Время жизни общего доступа в формате Duration (например, 'PT30M', 'PT1H', 'PT24H')", example = "PT30M")
        @Length(min = 1, message = "Время жизни доступа должно быть указано")
        String sharingLifetime) {
}
