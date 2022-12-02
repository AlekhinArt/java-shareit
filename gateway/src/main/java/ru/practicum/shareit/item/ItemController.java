package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemDto item) {
        log.info("Add item: {} , userId: {}", item, userId);
        checkUserId(userId);
        checkItem(item);

        return itemClient.add(userId, item);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemDto item,
                                         @PathVariable long itemId) {
        log.info("Update item: {} , userId: {}", item, userId);
        checkUserId(userId);
        checkUserId(itemId);

        return itemClient.update(userId, item, itemId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        log.info("getItem itemId: {} , userId: {}", itemId, userId);
        checkUserId(userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemCreator(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("GetItemCreator userId: {}, from: {}, size: {}", userId, from, size);
        checkUserId(userId);
        checkPageParam(from,size);
        return itemClient.getItemCreator(userId, from, size);
    }

    @GetMapping("search")

    public ResponseEntity<Object> findItem(@Valid
                                           @RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam String text,
                                           @RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("FindItem userId: {}, from: {}, size: {}, text: {}", userId, from, size , text);
        checkPageParam(from,size);
        checkUserId(userId);
        return itemClient.findItem(userId, text, from, size);

    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("itemId") long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("AddComment userId: {}, itemId: {}, Comment {}", userId, itemId, commentDto);
        checkUserId(userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    private void checkUserId(Long userId) {
        if (userId <= 0) {
            log.debug("Uncorrected id: {}", userId);
            throw new NotFoundException("Uncorrected id " + userId);
        }

    }

    private void checkItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.debug("Uncorrected item name: {}", itemDto.getName());
            throw new ValidationException("Uncorrected item name " + itemDto.getName());
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.debug("Uncorrected item description: {}", itemDto.getDescription());
            throw new ValidationException("Uncorrected item name " + itemDto.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            log.debug("Uncorrected field available: null");
            throw new ValidationException("Uncorrected field available: null");
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
