package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody Item item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody Item item,
                          @PathVariable long itemId) {
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemCreator(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsCreator(userId);
    }

    @GetMapping("search")

    public Collection<ItemDto> findItem(@Valid
                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam String text) {
        return itemService.findItem(text, userId);

    }


}
