package org.duahifnv.filehosting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duahifnv.exceptions.UserNotFoundException;
import org.duahifnv.filehosting.dto.user.UserBasicDto;
import org.duahifnv.filehosting.dto.user.UserFormDto;
import org.duahifnv.filehosting.dto.user.UsersBasicDto;
import org.duahifnv.filehosting.mapper.UserMapper;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/api/users")
    @ResponseStatus(HttpStatus.OK)
    public UsersBasicDto getAllUsers(Pageable pageable) {
        return new UsersBasicDto(
                userMapper.toBasicDtos(userService.findAll(pageable))
        );
    }

    @GetMapping("/api/user")
    @ResponseStatus(HttpStatus.OK)
    public UserBasicDto getUser(@RequestParam String email) {
        return userMapper.toBasicDto(userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email)));
    }

    @GetMapping("/api/user/me")
    @ResponseStatus(HttpStatus.OK)
    public UserFormDto getUserForm(@AuthenticationPrincipal User user) {
        return userMapper.toFormDto(user);
    }

    @PutMapping("/api/user/me")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@Valid @RequestBody UserFormDto updatedUser, @AuthenticationPrincipal User user) {
        userMapper.updateUser(user, updatedUser);
        userService.update(user);
    }
}
