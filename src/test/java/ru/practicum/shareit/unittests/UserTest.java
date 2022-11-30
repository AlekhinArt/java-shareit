package ru.practicum.shareit.unittests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest

public class UserTest {
    private static User user = new User(1L, "Test", "test@test.com");
    private static UserDto userDto = new UserDto(1L, "Test", "test@test.com");

    private UserService userService;
    private UserRepository mockUserRepository = Mockito.mock(UserRepository.class);

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void getAll() {
        Mockito
                .when(mockUserRepository.findAll())
                .thenReturn(List.of(user));
        List<UserDto> users = new ArrayList<>(userService.getAllUsers());
        Assertions.assertEquals(users.size(), 1);
        Assertions.assertEquals(users.get(0).getName(), userDto.getName());
        Assertions.assertEquals(users.get(0).getEmail(), userDto.getEmail());
    }

    @Test
    void getById() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        UserDto getUser = userService.getUser(1L);
        Assertions.assertEquals(getUser.getName(), userDto.getName());
        Assertions.assertEquals(getUser.getEmail(), userDto.getEmail());
    }

    @Test
    void getFailId() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUser(0L));
        Assertions.assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void create() {
        Mockito
                .when(mockUserRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto getUser = userService.createUser(new User());
        Assertions.assertEquals(getUser.getName(), userDto.getName());
        Assertions.assertEquals(getUser.getEmail(), userDto.getEmail());
    }

    @Test
    void update() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockUserRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto getUser = userService.updateUser(new User(), 1L);
        Assertions.assertEquals(getUser.getName(), userDto.getName());
    }

    @Test
    void updateUserNotFound() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(new User(), 0L));
        Assertions.assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void delete() {
        doNothing().when(mockUserRepository).deleteById(Mockito.anyLong());
        userService.deleteUser(1L);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
    }


    @Test
    void mapperToUser() {
        User user1 = UserMapper.dtoToUser(userDto);
        Assertions.assertEquals(user1.getName(), userDto.getName());
        Assertions.assertEquals(user1.getEmail(), userDto.getEmail());
        Assertions.assertEquals(user1.getId(), userDto.getId());

    }

    @Test
    void mapperToUserDto() {
        UserDto userDto1 = UserMapper.toUserDto(user);
        Assertions.assertEquals(userDto1.getName(), user.getName());
        Assertions.assertEquals(userDto1.getEmail(), user.getEmail());
        Assertions.assertEquals(userDto1.getId(), user.getId());

    }


}
