package org.duahifnv.filehosting.repository;

import org.duahifnv.filehosting.model.User;
import org.duahifnv.jwtauthstarter.repository.AbstractUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends AbstractUserRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
