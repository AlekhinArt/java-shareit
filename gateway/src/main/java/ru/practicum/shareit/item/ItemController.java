package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemDto item) {
        log.info("Add item: {} , userId: {}", item, userId);
        return itemClient.add(userId, item);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemDto item,
                                         @PositiveOrZero @PathVariable long itemId) {
        log.info("Update item: {} , userId: {}", item, userId);
        return itemClient.update(userId, item, itemId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItem(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                          @PositiveOrZero @PathVariable long itemId) {
        log.info("getItem itemId: {} , userId: {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemCreator(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GetItemCreator userId: {}, from: {}, size: {}", userId, from, size);
        return itemClient.getItemCreator(userId, from, size);
    }

    @GetMapping("search")

    public ResponseEntity<Object> findItem(@Valid
                                           @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam String text,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("FindItem userId: {}, from: {}, size: {}, text: {}", userId, from, size, text);
        if (text.isBlank()) return ResponseEntity.ok().body(Collections.emptyList());
        return itemClient.findItem(userId, text, from, size);

    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PositiveOrZero @PathVariable("itemId") long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("AddComment userId: {}, itemId: {}, Comment {}", userId, itemId, commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
