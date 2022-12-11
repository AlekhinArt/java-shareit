package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("getAllUsers");
        return UserMapper.mapToUserDto(users);
    }

    @Override
    public UserDto getUser(Long id) {
        log.info("getUser id: {}", id);
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
        log.info("createUser user: {}", user);
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
            log.debug("updateUser.AnybodyUseEmailOrNameException(имя или email)");
            throw new AnybodyUseEmailOrNameException("имя или email");
        }
        log.info("updateUser user: {}", user);
        return UserMapper.toUserDto(user);

    }

    @Override
    public void deleteUser(Long id) {
        try {
            log.info("deleteUser id: {}", id);
            userRepository.deleteById(id);
        } catch (Exception e) {
            log.debug("updateUser.NotFoundException(Пользователь не найден)");
            throw new NotFoundException("Пользователь не найден");
        }
    }

}
