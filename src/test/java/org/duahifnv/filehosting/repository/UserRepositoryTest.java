package org.duahifnv.filehosting.repository;

import org.duahifnv.filehosting.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Тестирование репозитория пользователей")
class UserRepositoryTest {
    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Должен возвращать пользователя по его ID")
    void findById_shouldFindUserById() {
        // given
        var user = new User();
        user.setUsername("user");
        user.setPassword("password");

        var persisted = entityManager.persist(user);

        // when
        var result = userRepository.findById(persisted.getId());

        // then
        assertThat(result).hasValue(persisted);
    }

    @Test
    @DisplayName("Должен возвращать пользователя по его Username")
    void findByUsername_shouldFindUserByUsername() {
        // given
        var user = new User();
        user.setUsername("user");
        user.setPassword("password");

        var persisted = entityManager.persist(user);

        // when
        var result = userRepository.findByUsername("user");

        // then
        assertThat(result).hasValue(persisted);
    }
}