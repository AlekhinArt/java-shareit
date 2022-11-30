package ru.practicum.shareit.unittests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest
public class ItemRequestTest {
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;

    private static ItemRequestService itemRequestService;
    private static User user;
    private static Item item;
    private static ItemRequest itemRequest;
    private static ItemRequestDto itemRequestDto;
    private static final LocalDateTime created = LocalDateTime.now();

    @BeforeEach
    private void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Test")
                .email("test@test.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("testItem")
                .description("test item description")
                .ownerId(1l)
                .available(true)
                .requestId(0L)
                .build();
        itemRequest = ItemRequest.builder()
                .requestId(1L)
                .description("test description")
                .requestor(1L)
                .created(created)
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requestor(1L)
                .created(created)
                .build();
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(mockItemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
    }

    @Test
    void createFailUser() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.createRequest(itemRequestDto, 0L )
        );
    }

    @Test
    void createUserNotFound() {
        Mockito
                .when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemRequestService.createRequest( itemRequestDto, 10L)
        );

    }

    @Test
    void getRequestsOk() {
        Mockito                .when(mockItemRequestRepository.findByRequestor(anyLong()))
                .thenReturn(List.of(itemRequest));
        Collection <ItemRequestDto> dtos = itemRequestService.getRequests(1L);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void getRequestsEmpty() {
        Mockito                .when(mockItemRequestRepository.findByRequestor(anyLong()))
                .thenReturn(Collections.emptyList());
        Collection<ItemRequestDto> dtos = itemRequestService.getRequests(1L);
        Assertions.assertEquals(dtos.size(), 0);
    }

    @Test
    void getAllOk() {
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorNot(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        Collection<ItemRequestDto> dtos = itemRequestService.getAllRequest(1L, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void getAllEmpty() {
        Mockito
                .when(mockItemRequestRepository.findAllByRequestorNot(anyLong(), any(Pageable.class)))
                .thenReturn(Page.empty());
        Collection<ItemRequestDto> dtos = itemRequestService.getAllRequest(1L, 1, 10);
        Assertions.assertEquals(dtos.size(), 0);
    }

    @Test
    void getAllFailParametersPage() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllRequest(1L, -1, 10)
        );

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllRequest(1L, 1, 0)
        );
    }

    @Test
    void mapperToRequestDto() {
        ItemRequestDto getRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        Assertions.assertEquals(getRequestDto.getId(), itemRequest.getRequestId());

    }

}
