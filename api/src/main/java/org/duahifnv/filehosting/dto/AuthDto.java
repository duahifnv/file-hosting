package org.duahifnv.filehosting.dto;

import jakarta.validation.constraints.NotBlank;
import org.duahifnv.jwtauthstarter.dto.AbstractAuthDto;
import org.hibernate.validator.constraints.Length;

public class AuthDto extends AbstractAuthDto {
    public AuthDto(String username, String password) {
        super(username, password);
    }

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Length(min = 4, message = "Длина имени от 4 символов")
    public String getUsername() {
        return super.getUsername();
    }

    @NotBlank(message = "Пароль не может быть пустым")
    @Length(min = 6, message = "Длина пароля от 6 символов")
    public String getPassword() {
        return super.getPassword();
    }
}
