package org.duahifnv.filehosting.dto;

import jakarta.validation.constraints.NotBlank;
import org.duahifnv.jwtauthstarter.dto.AbstractUserDto;
import org.hibernate.validator.constraints.Length;

public class UserDto extends AbstractUserDto {
    public UserDto(@NotBlank @Length(min = 6) String username,
                   @NotBlank @Length(min = 6) String password) {
        super(username, password);
    }
}
