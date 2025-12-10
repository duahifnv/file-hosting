package org.duahifnv.filehosting.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.duahifnv.jwtauthstarter.dto.AbstractAuthDto;
import org.hibernate.validator.constraints.Length;

@Schema(description = "DTO для аутентификации пользователя")
public class AuthDto extends AbstractAuthDto {
    public AuthDto(String username, String password) {
        super(username, password);
    }

    @Schema(description = "Имя пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "testuser1", minLength = 4)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Length(min = 4, message = "Длина имени от 4 символов")
    public String getUsername() {
        return super.getUsername();
    }

    @Schema(description = "Пароль пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "testpass1", minLength = 6)
    @NotBlank(message = "Пароль не может быть пустым")
    @Length(min = 6, message = "Длина пароля от 6 символов")
    public String getPassword() {
        return super.getPassword();
    }
}
