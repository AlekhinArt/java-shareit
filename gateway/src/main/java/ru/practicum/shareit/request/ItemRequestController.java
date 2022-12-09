package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> createRequest(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("createRequest itemRequest: {}, userId: {}", itemRequestDto, userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);

    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("getRequests userId: {}", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("getAllRequests userId: {}, page param. from: {}, size: {}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PositiveOrZero @PathVariable("requestId") long requestId) {
        log.info("getRequest userId: {}, requestId: {}", userId, requestId);
        return itemRequestClient.getRequest(userId, requestId);
    }

}
