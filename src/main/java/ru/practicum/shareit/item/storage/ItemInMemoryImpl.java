package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;


public class ItemInMemoryImpl implements ItemStorage {

    private final UserStorage userStorage;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> userWithItem = new HashMap<>();
    private long id = 0;

    public ItemInMemoryImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Item addNewItem(long userId, Item item) {
        item.setId(getId());
//        item.setOwner(userStorage.getUser(userId));
        items.put(item.getId(), item);
        List<Long> abc = new ArrayList<>();
        abc.add(item.getId());
        userWithItem.put(userId, abc);
        return item;
    }

    @Override
    public Item updateItem(Item item, long userId, long itemId) {
        if (userWithItem.containsKey(userId) && userWithItem.get(userId).contains(itemId)) {
            Item oldItem = items.get(itemId);
            if (item.getName() == null) item.setName(oldItem.getName());
            if (item.getDescription() == null) item.setDescription(oldItem.getDescription());
            if (item.getAvailable() == null) item.setAvailable(oldItem.getAvailable());
//            item.setOwner(oldItem.getOwner());
//            item.setRequest(oldItem.getRequest());
            item.setId(itemId);
            items.put(itemId, item);
            return item;
        } else throw new NoAccessException("Нет доступа к этому предмету");
    }

    @Override
    public Item getItem(long userId, long itemId) {
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getItemsCreator(long userId) {
        Collection<Item> itemsCreator = new ArrayList<>();
        for (Long itemId : userWithItem.get(userId)) {
            itemsCreator.add(items.get(itemId));
        }
        return itemsCreator;
    }

    @Override
    public Collection<Item> findItem(String description, long userId) {
        Collection<Item> availableItems = new ArrayList<>();
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(description.toLowerCase())
                    || item.getDescription().toLowerCase().contains(description.toLowerCase()))
                    && !description.isBlank()
                    && item.getAvailable()) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }

    private Long getId() {
        return ++id;
    }

}
