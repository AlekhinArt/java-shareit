package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("getAllUsers");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        log.info("createUser user : {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Name is blank");
            throw new ValidationException("Name is blank");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.debug("email is blank");
            throw new ValidationException("email is blank.");
        }
        return userClient.createUser(user);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(@Valid @PathVariable long id) {
        log.info("getUser userId : {}", id);
        checkId(id);
        return userClient.getUser(id);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestBody UserDto user, @PathVariable long id) {
        log.info("update userId : {}, update user {}", id, user);
        checkId(id);
        return userClient.update(user, id);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@Valid @PathVariable long id) {
        log.info("deleteUser userId: {}", id);
        checkId(id);
        userClient.deleteUser(id);
    }

    private void checkId(long id) {
        if (id <= 0) {
            log.debug("Uncorrected id: {}", id);
            throw new NotFoundException("Uncorrected id " + id);
        }
    }
}
