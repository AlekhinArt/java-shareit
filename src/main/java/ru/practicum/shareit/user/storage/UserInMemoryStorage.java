package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserInMemoryStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User getUser(long id) {
        return users.getOrDefault(id, null);
    }

    @Override
    public Collection<User> getAllUsers() {
//        Collection<UserDto> usersDto = new ArrayList<>();
//        for (User user : users.values()) {
//            usersDto.add(UserMapper.toUserDto(user));
//        }
        return users.values();
    }

    @Override
    public User createNewUser(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user, long id) {
        User oldUser = users.get(id);
        if (user.getName() == null) user.setName(oldUser.getName());
        if (user.getEmail() == null) user.setEmail(oldUser.getEmail());
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    private Long getId() {
        return ++id;
    }

}
