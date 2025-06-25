package org.duahifnv.filehosting.repository;

import org.duahifnv.filehosting.config.TestClockConfig;
import org.duahifnv.filehosting.model.FileMeta;
import org.duahifnv.filehosting.model.ShareMode;
import org.duahifnv.filehosting.model.SharedMeta;
import org.duahifnv.filehosting.model.User;
import org.duahifnv.jwtauthstarter.JwtAuthAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({JwtAuthAutoConfiguration.class, TestClockConfig.class})
class SharedMetaRepositoryTest {
    @Autowired
    private SharedMetaRepository sharedMetaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private Clock clock;

    @Test
    void findFileMetasByUser_returnsSharedFileMetasForUser() {
        // given
        var user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        var fileMeta = new FileMeta();
        fileMeta.setUser(user);
        fileMeta.setOriginalName("file.txt");
        fileMeta.setContentType("text/plain");
        fileMeta.setOriginalSize(123L);
        fileMeta.setBucket("bucket");
        fileMeta.setObjectPath("path/to/file");
        fileMeta.setEncryptionKey(new byte[]{1,2,3});
        fileMeta.setIv(new byte[]{4,5,6});
        fileMeta.setCreatedAt(OffsetDateTime.now());
        entityManager.persist(fileMeta);

        var sharedMeta = new SharedMeta();
        sharedMeta.setMetadata(fileMeta);
        sharedMeta.setShareMode(ShareMode.SELECTIVE);
        sharedMeta.setSharedAt(OffsetDateTime.now(clock));
        sharedMeta.setSharedUsers(List.of(user));
        entityManager.persist(sharedMeta);
        entityManager.flush();

        // when
        var result = sharedMetaRepository.findFileMetasByUser(user, Pageable.unpaged());

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(fileMeta.getOriginalName(), result.getContent().get(0).getOriginalName());
    }

    @Test
    void findFileMetasByUser_returnsEmptyForUserNotInSharedUsers() {
        // given
        var owner = new User();
        owner.setUsername("owner");
        owner.setEmail("owner@example.com");
        owner.setPassword("password");
        entityManager.persist(owner);

        var otherUser = new User();
        otherUser.setUsername("other");
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("password");
        entityManager.persist(otherUser);

        var fileMeta = new FileMeta();
        fileMeta.setUser(owner);
        fileMeta.setOriginalName("file.txt");
        fileMeta.setContentType("text/plain");
        fileMeta.setOriginalSize(123L);
        fileMeta.setBucket("bucket");
        fileMeta.setObjectPath("path/to/file");
        fileMeta.setEncryptionKey(new byte[]{1,2,3});
        fileMeta.setIv(new byte[]{4,5,6});
        fileMeta.setCreatedAt(OffsetDateTime.now(clock));
        entityManager.persist(fileMeta);

        var sharedMeta = new SharedMeta();
        sharedMeta.setMetadata(fileMeta);
        sharedMeta.setShareMode(ShareMode.SELECTIVE);
        sharedMeta.setSharedAt(OffsetDateTime.now());
        sharedMeta.setSharedUsers(List.of(owner));
        entityManager.persist(sharedMeta);
        entityManager.flush();

        // when
        var result = sharedMetaRepository.findFileMetasByUser(otherUser, Pageable.unpaged());

        // then
        assertEquals(0, result.getTotalElements());
    }
}