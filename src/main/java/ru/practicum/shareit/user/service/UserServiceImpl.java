package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AnybodyUseEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.AnybodyUseNameException;
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
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден")));
    }

    @Override
    public UserDto createUser(User user) {
        //checkUser(user);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(User user, long id) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        checkUser(user);
        if (user.getName() == null) user.setName(oldUser.getName());
        if (user.getEmail() == null) user.setEmail(oldUser.getEmail());
        user.setId(id);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void checkUser(User user) {
        Collection<User> oldUsers = userRepository.findAll();
        for (User oldUser : oldUsers) {
            if (oldUser.getName().equals(user.getName())) {
                throw new AnybodyUseNameException("Имя занято");
            }
            if (oldUser.getEmail().equals(user.getEmail())) {
                throw new AnybodyUseEmailException("Email уже занят");
            }
        }
    }
}
