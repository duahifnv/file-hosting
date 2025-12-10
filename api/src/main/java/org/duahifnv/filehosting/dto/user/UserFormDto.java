package org.duahifnv.filehosting.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "DTO для формы редактирования пользователя")
public record UserFormDto(
        @Schema(description = "Имя пользователя для входа (только для чтения)", example = "testuser1", accessMode = Schema.AccessMode.READ_ONLY)
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String username,

        @Schema(description = "Email пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "user1@mail.ru")
        @NotBlank(message = "Почта не может быть пустой")
        @Email
        String email,

        @Schema(description = "Имя пользователя", example = "Maksim")
        String firstname,

        @Schema(description = "Фамилия пользователя", example = "Fomintsev")
        String lastname,

        @Schema(description = "Пароль пользователя (только для записи, опционально)", example = "testpass1", minLength = 6, accessMode = Schema.AccessMode.WRITE_ONLY)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Length(min = 6, message = "Длина пароля от 6 символов")
        String password) {
}
