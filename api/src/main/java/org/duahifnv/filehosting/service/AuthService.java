package org.duahifnv.filehosting.service;

import org.duahifnv.filehosting.dto.user.AuthDto;
import org.duahifnv.filehosting.dto.user.RegisterDto;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.jwtauthstarter.auth.AbstractAuthService;
import org.duahifnv.jwtauthstarter.auth.AbstractUserService;
import org.duahifnv.jwtauthstarter.jwt.JwtService;
import org.duahifnv.jwtauthstarter.mapper.AbstractUserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService extends AbstractAuthService<RegisterDto, AuthDto, User> {
    public AuthService(JwtService jwtService,
                       AbstractUserService<? extends UserDetails, ?> userService,
                       AuthenticationManager authenticationManager,
                       AbstractUserMapper<RegisterDto, AuthDto, ? extends UserDetails> userMapper) {
        super(jwtService, (AbstractUserService<UserDetails, ?>) userService, authenticationManager, userMapper);
    }
}
