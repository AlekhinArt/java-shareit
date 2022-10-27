package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.WhoUseEmailOrNameException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> usersDto = new ArrayList<>();
        for (User user : userStorage.getAllUsers()) {
            usersDto.add(UserMapper.toUserDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto getUser(Long id) {
        findUserId(id);
        return UserMapper.toUserDto(userStorage.getUser(id));
    }

    @Override
    public UserDto createUser(User user) {
        checkUser(user);
        return UserMapper.toUserDto(userStorage.createNewUser(user));
    }

    @Override
    public UserDto updateUser(User user, long id) {
        findUserId(id);
        checkUser(user);
        return UserMapper.toUserDto(userStorage.updateUser(user, id));
    }

    @Override
    public void deleteUser(Long id) {
        findUserId(id);
        userStorage.deleteUser(id);
    }

    private void checkUser(User user) {
        Collection<User> oldUsers = userStorage.getAllUsers();
        for (User oldUser : oldUsers) {
            if (oldUser.getName().equals(user.getName())
                    || oldUser.getEmail().equals(user.getEmail())) {
                throw new WhoUseEmailOrNameException("Занято");
            }
        }
    }

    private void findUserId(Long id) {
        if (userStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }


}
