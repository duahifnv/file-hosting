package org.duahifnv.filehosting.mapper;

import org.duahifnv.filehosting.dto.user.AuthDto;
import org.duahifnv.filehosting.dto.user.RegisterDto;
import org.duahifnv.filehosting.dto.user.UserBasicDto;
import org.duahifnv.filehosting.dto.user.UserFormDto;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.jwtauthstarter.mapper.AbstractUserMapper;
import org.mapstruct.*;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class UserMapper extends AbstractUserMapper<RegisterDto, AuthDto, User> {
    public abstract UserFormDto toFormDto(User user);
    public abstract UserBasicDto toBasicDto(User user);
    public abstract List<UserBasicDto> toBasicDtos(List<User> users);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    public abstract void updateUser(@MappingTarget User user, UserFormDto updatedUserInfo);
}
