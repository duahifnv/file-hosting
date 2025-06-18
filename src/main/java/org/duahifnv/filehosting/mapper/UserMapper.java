package org.duahifnv.filehosting.mapper;

import org.duahifnv.filehosting.dto.AuthDto;
import org.duahifnv.filehosting.dto.UserDto;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.jwtauthstarter.mapper.AbstractUserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserMapper extends AbstractUserMapper<UserDto, AuthDto, User> {
}
