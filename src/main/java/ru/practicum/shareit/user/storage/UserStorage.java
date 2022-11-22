package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;


public interface UserStorage {
    User getUser(long id);

    Collection<User> getAllUsers();

    User createNewUser(User user);

    User updateUser(User user, long id);

    void deleteUser(long id);


}
