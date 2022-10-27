package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Component
public interface ItemStorage {
    Item addNewItem(long userId, Item item);

    Item updateItem(Item item, long userId, long itemId);

    Item getItem(long userId, long itemId);

    Collection<Item> getItemsCreator(long userId);

    Collection<Item> findItem(String description, long userId);
}
