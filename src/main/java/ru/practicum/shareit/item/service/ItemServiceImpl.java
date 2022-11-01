package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(UserService userService, ItemStorage itemStorage) {
        this.userService = userService;
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto addNewItem(long userId, Item item) {
        checkUser(userId);
        return ItemMapper.toItemDto(itemStorage.addNewItem(userId, item));
    }

    @Override
    public ItemDto updateItem(Item item, long userId, long itemId) {
        checkUser(userId);
        checkItem(itemId, userId);
        return ItemMapper.toItemDto(itemStorage.updateItem(item, userId, itemId));
    }

    @Override
    public ItemDto getItem(long userId, long itemId) {
        checkUser(userId);
        return ItemMapper.toItemDto(itemStorage.getItem(userId, itemId));
    }

    @Override
    public Collection<ItemDto> getItemsCreator(long userId) {
        checkUser(userId);
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemStorage.getItemsCreator(userId)) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> findItem(String description, long userId) {
        checkUser(userId);
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemStorage.findItem(description, userId)) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    private void checkUser(long userId) {
        if (userService.getUser(userId) == null) throw new UserNotFoundException("Пользователя нет");
    }

    private void checkItem(long itemId, long userId) {
        if (itemStorage.getItem(userId, itemId) == null) throw new ItemNotFoundException("Такого предмета нет");
    }
}
