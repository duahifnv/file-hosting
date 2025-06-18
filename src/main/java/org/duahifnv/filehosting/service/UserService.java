package org.duahifnv.filehosting.service;

import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.repository.UserRepository;
import org.duahifnv.jwtauthstarter.auth.AbstractUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService extends AbstractUserService<User, UUID> {
    public UserService(UserRepository userRepository) {
        super(userRepository);
    }
}
