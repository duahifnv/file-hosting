package org.duahifnv.filehosting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duahifnv.filehosting.dto.user.AuthDto;
import org.duahifnv.filehosting.dto.user.RegisterDto;
import org.duahifnv.filehosting.service.AuthService;
import org.duahifnv.jwtauthstarter.jwt.JwtDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/api/auth")
    @ResponseStatus(HttpStatus.OK)
    public JwtDto authenticate(@RequestBody @Valid AuthDto authDto) {
        return authService.authenticateUser(authDto);
    }

    @PostMapping("/api/register")
    @ResponseStatus(HttpStatus.CREATED)
    public JwtDto register(@RequestBody @Valid RegisterDto registerDto) {
        return authService.registerNewUser(registerDto);
    }
}
