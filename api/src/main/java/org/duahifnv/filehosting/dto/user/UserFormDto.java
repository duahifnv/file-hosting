package org.duahifnv.filehosting.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserFormDto(@JsonProperty(access = JsonProperty.Access.READ_ONLY)
                            String username,
                          @NotBlank(message = "Почта не может быть пустой")
                          @Email
                            String email,
                            String firstname,
                            String lastname,
                          @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                          @Length(min = 6, message = "Длина пароля от 6 символов")
                            String password) {
}
