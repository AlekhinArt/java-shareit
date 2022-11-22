package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto addNewItem(long userId, Item item);

    ItemDto updateItem(Item item, long userId, long itemId);

    ItemDto getItem(long userId, long itemId);

    Collection<ItemDto> getItemsCreator(long userId);

    Collection<ItemDto> findItem(String description, long userId);

    CommentDtoResponse addComment(long userId, long itemId, CommentDto commentDto);
}
