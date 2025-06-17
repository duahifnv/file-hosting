package org.duahifnv.filehosting.repository;

import org.duahifnv.filehosting.model.User;
import org.duahifnv.jwtauthstarter.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepositoryImpl extends UserRepository<User, UUID> {
}
