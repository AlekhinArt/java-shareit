package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.valid.Create;
import ru.practicum.shareit.valid.Update;

import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("getAllUsers");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto user) {
        log.info("createUser user : {}", user);
        return userClient.createUser(user);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(@PositiveOrZero @PathVariable long id) {
        log.info("getUser userId : {}", id);
        return userClient.getUser(id);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@Validated(Update.class) @RequestBody UserDto user,
                                         @PositiveOrZero @PathVariable long id) {
        log.info("update userId : {}, update user {}", id, user);
        return userClient.update(user, id);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PositiveOrZero @PathVariable long id) {
        log.info("deleteUser userId: {}", id);
        userClient.deleteUser(id);
    }

}
