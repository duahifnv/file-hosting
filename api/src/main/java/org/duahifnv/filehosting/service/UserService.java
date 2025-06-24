package org.duahifnv.filehosting.service;

import org.duahifnv.exceptions.UserAlreadyExistsException;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.repository.UserRepository;
import org.duahifnv.jwtauthstarter.auth.AbstractUserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends AbstractUserService<User, UUID> {
    private final UserRepository repository;

    public UserService(UserRepository userRepository) {
        super(userRepository);
        repository = userRepository;
    }

    public List<User> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream().toList();
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public void update(User user) {
        findByEmail(user.getEmail()).ifPresent(existing -> {
            if (!existing.getUsername().equals(user.getUsername())) {
                throw new UserAlreadyExistsException(user.getEmail());
            }
        });

        super.save(user);
    }
}
