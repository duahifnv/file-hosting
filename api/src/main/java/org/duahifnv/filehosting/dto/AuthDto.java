package org.duahifnv.filehosting.dto;

import jakarta.validation.constraints.NotBlank;
import org.duahifnv.jwtauthstarter.dto.AbstractAuthDto;
import org.hibernate.validator.constraints.Length;

public class AuthDto extends AbstractAuthDto {
    public AuthDto(@NotBlank @Length(min = 6) String username,
                   @NotBlank @Length(min = 6) String password) {
        super(username, password);
    }
}
