package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    Collection<ItemRequestDto> getRequests(Long userId);

    Collection<ItemRequestDto> getAllRequest(Long userId, int from, int size);

    ItemRequestDto getRequest(Long requestId, Long userId);
}
