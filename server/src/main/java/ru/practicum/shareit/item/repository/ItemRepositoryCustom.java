package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemRepositoryCustom {
    ItemDto getByIdForResponse(long userId, long id);
}
