package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("createRequest itemRequest: {}, userId: {}", itemRequestDto, userId);
        checkId(userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);

    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("getRequests userId: {}", userId);
        checkId(userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("getAllRequests userId: {}, page param. from: {}, size: {}", userId, from, size);
        checkId(userId);
        checkPageParam(from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("requestId") long requestId) {
        log.info("getRequest userId: {}, requestId: {}", userId, requestId);
        checkId(userId);
        checkId(requestId);
        return itemRequestClient.getRequest(requestId, userId);
    }

    private void checkId(Long userId) {
        if (userId <= 0) {
            log.debug("Uncorrected id: {}", userId);
            throw new NotFoundException("Uncorrected id " + userId);
        }
    }

    private void checkPageParam(int from, int size) {
        if (from < 0) {
            log.debug("Uncorrected field from: {}", from);
            throw new ValidationException("Uncorrected field from : from < 0");
        }
        if (size <= 0) {
            log.debug("Uncorrected field size: {}", size);
            throw new ValidationException("Uncorrected field from : size <= 0");
        }
    }

}
