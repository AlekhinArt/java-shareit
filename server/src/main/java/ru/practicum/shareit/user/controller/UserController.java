package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("getAllUsers");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@RequestBody User user) {
        log.info("createUser user : {}", user);
        return userService.createUser(user);
    }

    @GetMapping("{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("getUser userId : {}", id);
        return userService.getUser(id);
    }

    @PatchMapping("{id}")
    public UserDto update(@RequestBody User user, @PathVariable long id) {
        log.info("update userId : {}, update user {}", id, user);
        return userService.updateUser(user, id);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("deleteUser userId: {}", id);
        userService.deleteUser(id);
    }

}
