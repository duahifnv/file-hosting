package org.duahifnv.filehosting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.duahifnv.filehosting.dto.user.UserFormDto;
import org.duahifnv.filehosting.filter.AuthFilter;
import org.duahifnv.filehosting.mapper.UserMapper;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AuthFilter.class})
)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Disabled
    void getAllUsers_returnsAllUsers_paged() throws Exception {
        var users = List.of(
                createUser("user1", "user1@email.com"),
                createUser("user2", "user2@email.com")
        );
        var userDtos = List.of(
                createUserInfoDto("user1@email.com"),
                createUserInfoDto("user2@email.com")
        );
        var pageNumber = 0;
        var pageSize = 3;
        var pageRequest = PageRequest.of(pageNumber, pageSize);

        when(userService.findAll(pageRequest)).thenReturn(users);
        when(userMapper.toDtos(users)).thenReturn(userDtos);

        mvc.perform(get("/api/users")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("users").isArray())
                .andExpect(jsonPath("users[0].username").value("user1"));

        verify(userService).findAll(eq(pageRequest));
        verify(userMapper).toDtos(users);
    }

    @Test
    void getUser_returnsUserByEmail() throws Exception {
        var email = "user1@email.com";
        var userDto = createUserInfoDto(email);

        var user = mock(User.class);

        when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toFormDto(user)).thenReturn(userDto);

        mvc.perform(get("/api/user").param("email", "user1@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("user1@email.com"));
    }

    @Test
    void getUser_returns404_whenNotFound() throws Exception {
        when(userService.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        mvc.perform(get("/api/user").param("email", "notfound@email.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser_returnsAuthenticatedUserForm() throws Exception {
        // given
        var userDto = createUserInfoDto("user1@email.com");
        when(userMapper.toFormDto(nullable(User.class))).thenReturn(userDto);

        // when
        mvc.perform(get("/api/user/me"))
                .andExpect(status().isOk());

        // then
        verify(userMapper).toFormDto(nullable(User.class));
    }

    @Test
    @Disabled
    void updateUser_updatesUser() throws Exception {
        var updatedUserDto = createUserInfoDto("user1@email.com");

        mvc.perform(put("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk());

        verify(userMapper).updateUser(nullable(User.class), any(UserFormDto.class));
        verify(userService).save(nullable(User.class));
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstname("First");
        user.setLastname("Last");
        user.setPassword("password");
        return user;
    }

    private UserFormDto createUserInfoDto(String email) {
        return new UserFormDto(email, "First", "Last", "Password");
    }
}
