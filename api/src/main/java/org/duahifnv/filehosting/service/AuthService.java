package org.duahifnv.filehosting.service;

import org.duahifnv.filehosting.dto.AuthDto;
import org.duahifnv.filehosting.dto.UserDto;
import org.duahifnv.jwtauthstarter.auth.AbstractAuthService;
import org.duahifnv.jwtauthstarter.auth.AbstractUserService;
import org.duahifnv.jwtauthstarter.jwt.JwtService;
import org.duahifnv.jwtauthstarter.mapper.AbstractUserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService extends AbstractAuthService<UserDto, AuthDto> {
    public AuthService(JwtService jwtService,
                       AbstractUserService<? extends UserDetails, ?> userService,
                       AuthenticationManager authenticationManager,
                       AbstractUserMapper<UserDto, AuthDto, ? extends UserDetails> userMapper,
                       PasswordEncoder passwordEncoder) {
        super(jwtService, (AbstractUserService<UserDetails, ?>) userService, authenticationManager, userMapper, passwordEncoder);
    }
}
