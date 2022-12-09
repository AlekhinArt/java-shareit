package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @RequestBody Item item) {
        log.info("Add item: {} , userId: {}", item, userId);
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody Item item,
                          @PathVariable long itemId) {
        log.info("Update item: {} , userId: {}", item, userId);
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("getItem itemId: {} , userId: {}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemCreator(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(required = false, defaultValue = "0") int from,
                                              @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("GetItemCreator userId: {}, from: {}, size: {}", userId, from, size);
        return itemService.getItemsCreator(userId, from, size);
    }

    @GetMapping("search")
    public Collection<ItemDto> findItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam String text,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("FindItem userId: {}, from: {}, size: {}, text: {}", userId, from, size, text);
        return itemService.findItem(text, userId, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDtoResponse addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable("itemId") long itemId,
                                         @RequestBody CommentDto commentDto) {
        log.info("AddComment userId: {}, itemId: {}, Comment {}", userId, itemId, commentDto);
        return itemService.addComment(userId, itemId, commentDto);
    }

}
