package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Service
public interface ItemService {

    ItemDto addNewItem(long userId, Item item);

    ItemDto updateItem(Item item, long userId, long itemId);

    ItemDto getItem(long userId, long itemId);

    Collection<ItemDto> getItemsCreator(long userId);

    Collection<ItemDto> findItem(String description, long userId);
}
