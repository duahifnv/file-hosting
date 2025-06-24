package org.duahifnv.filehosting.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.duahifnv.jwtauthstarter.dto.AbstractUserDto;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class RegisterDto extends AbstractUserDto {
    @NotBlank(message = "Почта должна быть заполнена")
    @Email
    private String email;
    @Size(min = 1, message = "Имя пользователя не может быть пустым")
    private String firstname;
    @Size(min = 1, message = "Фамилия пользователя не может быть пустой")
    private String lastname;

    public RegisterDto(String username, String password) {
        super(username, password);
    }

    @NotBlank(message = "Идентификатор пользователя должен быть заполнен")
    @Length(min = 4, message = "Длина идентификатора от 4 символов")
    public String getUsername() {
        return super.getUsername();
    }

    @NotBlank(message = "Пароль должен быть заполнен")
    @Length(min = 6, message = "Длина пароля от 6 символов")
    public String getPassword() {
        return super.getPassword();
    }
}
