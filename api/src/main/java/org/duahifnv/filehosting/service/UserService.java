package org.duahifnv.filehosting.service;

import org.duahifnv.exceptions.UserAlreadyExistsException;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.filehosting.repository.UserRepository;
import org.duahifnv.jwtauthstarter.auth.AbstractUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends AbstractUserService<User, UUID> {
    private final UserRepository repository;
    protected static final Logger log = LoggerFactory.getLogger(UserService.class);

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

    public List<User> findUsersByEmails(List<String> userEmails) {
        if (userEmails == null) {
            return List.of();
        }
        return userEmails.stream()
                .map(email -> findByEmail(email).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public void update(User user) {
        findByEmail(user.getEmail()).ifPresent(existing -> {
            if (!existing.getUsername().equals(user.getUsername())) {
                throw new UserAlreadyExistsException(user.getEmail());
            }
        });

        super.save(user);
        log.debug("User: Пользователь [{}] обновлен", user.getUsername());
    }
}
