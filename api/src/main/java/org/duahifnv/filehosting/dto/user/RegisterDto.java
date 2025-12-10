package org.duahifnv.filehosting.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.duahifnv.jwtauthstarter.dto.AbstractUserDto;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Schema(description = "DTO для регистрации нового пользователя")
public class RegisterDto extends AbstractUserDto {
    @Schema(description = "Email пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    @NotBlank(message = "Почта должна быть заполнена")
    @Email
    private String email;

    @Schema(description = "Имя пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "Иван")
    @Size(min = 1, message = "Имя пользователя не может быть пустым")
    private String firstname;

    @Schema(description = "Фамилия пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "Иванов")
    @Size(min = 1, message = "Фамилия пользователя не может быть пустой")
    private String lastname;

    public RegisterDto(String username, String password) {
        super(username, password);
    }

    @Schema(description = "Имя пользователя для входа", requiredMode = Schema.RequiredMode.REQUIRED, example = "john_doe", minLength = 4)
    @NotBlank(message = "Идентификатор пользователя должен быть заполнен")
    @Length(min = 4, message = "Длина идентификатора от 4 символов")
    public String getUsername() {
        return super.getUsername();
    }

    @Schema(description = "Пароль пользователя", requiredMode = Schema.RequiredMode.REQUIRED, example = "password123", minLength = 6)
    @NotBlank(message = "Пароль должен быть заполнен")
    @Length(min = 6, message = "Длина пароля от 6 символов")
    public String getPassword() {
        return super.getPassword();
    }
}
