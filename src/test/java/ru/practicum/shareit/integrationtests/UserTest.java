package ru.practicum.shareit.integrationtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.AnybodyUseEmailOrNameException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserTest {


    private final EntityManager entityManager;
    private final UserService userService;
    private static User user;

    @BeforeEach
    @Sql({"/schema.sql"})
    public void beforeEach() {
        user = User.builder()
                .name("Test")
                .email("test@test.ru")
                .build();
    }

    @AfterEach
    @Sql({"/clean.sql"})
    void clean() {
    }


    @Test
    void saveUser() {
        userService.createUser(user);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User testUser = query.setParameter("email", user.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(testUser.getName()));
        assertThat(user.getEmail(), equalTo(testUser.getEmail()));
    }

    @Test
    void saveUserFailName() {
        userService.createUser(user);
        Assertions.assertThrows(
                AnybodyUseEmailOrNameException.class,
                () -> userService.createUser(User.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .build()));
    }

    @Test
    void updateUser() {
        UserDto testUser = userService.createUser(user);
        testUser.setEmail("test2@test.ru");
        userService.updateUser(UserMapper.dtoToUser(testUser), testUser.getId());

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", testUser.getId()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(testUser.getName()));
        assertThat(user.getEmail(), equalTo(testUser.getEmail()));
    }

    @Test
    void findAll() {
        userService.createUser(user);
        userService.createUser(User.builder()
                .email("test2@test.ru")
                .name("test2").build());

        TypedQuery<User> query = entityManager.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(users.size(), equalTo(2));
    }

    @Test
    void findById() {
        UserDto testUser = userService.createUser(user);

        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", testUser.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(testUser.getId()));
        assertThat(user.getName(), equalTo(testUser.getName()));
        assertThat(user.getEmail(), equalTo(testUser.getEmail()));
    }
}
