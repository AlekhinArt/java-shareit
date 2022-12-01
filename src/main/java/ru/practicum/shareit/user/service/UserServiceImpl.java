package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AnybodyUseEmailOrNameException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Override
    public UserDto createUser(User user) {
        User newUser;
        try {
            newUser = userRepository.save(user);
        } catch (Exception e) {
            throw new AnybodyUseEmailOrNameException("имя или email");
        }
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(User user, long id) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (user.getName() == null) user.setName(oldUser.getName());
        if (user.getEmail() == null) user.setEmail(oldUser.getEmail());
        user.setId(id);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new AnybodyUseEmailOrNameException("имя или email");
        }
        return UserMapper.toUserDto(user);

    }

    @Override
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void checkUser(User user) {
        Collection<User> oldUsers = userRepository.findAll();
        for (User oldUser : oldUsers) {
            if (oldUser.getName().equals(user.getName())) {
                throw new AnybodyUseEmailOrNameException("это имя: " + user.getName());
            }
            if (oldUser.getEmail().equals(user.getEmail())) {
                throw new AnybodyUseEmailOrNameException("этот Email: " + user.getEmail());
            }
        }
    }
}
