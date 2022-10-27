package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Component
public interface UserStorage {
    User getUser(long id);

    Collection<User> getAllUsers();

    User createNewUser(User user);

    User updateUser(User user, long id);

    void deleteUser(long id);


}
