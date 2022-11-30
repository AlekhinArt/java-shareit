package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        getUser(userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(ItemRequest.builder()
                .created(LocalDateTime.now())
                .description(itemRequestDto.getDescription())
                .requestor(userId)
                .build()));
    }


    @Override
    public Collection<ItemRequestDto> getRequests(Long userId) {
        getUser(userId);
        Collection<ItemRequest> requests = itemRequestRepository.findByRequestor(userId);
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemRequestDto> itemRequestDto = requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        addItemsList(itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public Collection<ItemRequestDto> getAllRequest(Long userId, int from, int size) {
        System.out.println("PNX");
        getUser(userId);
        checkPageParam(from, size);
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        Page<ItemRequest> pages = itemRequestRepository.findAllByRequestorNot(userId, pageable);
        List<ItemRequestDto> itemRequestDto = pages.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        addItemsList(itemRequestDto);
        return itemRequestDto;
    }


    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        addItemsList(List.of(itemRequestDto));
        return itemRequestDto;

    }


    private void addItemsList(List<ItemRequestDto> requestsDto) {
        for (ItemRequestDto itemRequestDto : requestsDto) {
            Collection<Item> items = itemRepository.findByRequest_Id(itemRequestDto.getId());
            itemRequestDto.setItems(ItemMapper.mapToItemDto(items));
        }
    }

    private void checkPageParam(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс первого элемента должен быть больше 0");
        }
        if (size <= 0) {
            throw new ValidationException("Количество предметов должно быть больше 0");
        }
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

    }


}
