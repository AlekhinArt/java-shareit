package ru.practicum.shareit.jpatest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
public class UserTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    private static User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("test")
                .email("test@test.ru")
                .build();
    }

    @Test
    void save() {
        User user1 = userRepository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user1.getName(), "test");
    }

    @Test
    void update() {
        userRepository.save(user);
        user.setName("tester");
        User user1 = userRepository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user1.getName(), "tester");
    }

    @Test
    void load() {
        Assertions.assertNotNull(entityManager);
    }

}
