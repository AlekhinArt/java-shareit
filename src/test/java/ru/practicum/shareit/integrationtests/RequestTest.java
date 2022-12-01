package ru.practicum.shareit.integrationtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

public class RequestTest {

    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private static Item item;
    private static ItemDto itemDto;
    private static User user;
    private static ItemRequestDto requestDto;
    private static final LocalDateTime created = LocalDateTime.now();

    @BeforeEach
    @Sql({"/schema.sql"})
    public void beforeEach() {
        item = Item.builder()
                .name("test")
                .description("test description")
                .ownerId(1L)
                .available(true)
                .requestId(0L)
                .build();
        itemDto = ItemMapper.toItemDto(item);
        user = User.builder()
                .name("test")
                .email("test@test.ru")
                .build();
        requestDto = ItemRequestDto.builder()
                .description("description")
                .requestor(1L)
                .created(created)
                .build();
    }

    @AfterEach
    @Sql({"/clean.sql"})
    void clean() {
    }

    @Test
    void save() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(getUser.getId());
        itemService.addNewItem(getUser.getId(), item);
        requestDto.setRequestor(getUser.getId());
        itemRequestService.createRequest(requestDto, getUser.getId());

        TypedQuery<ItemRequest> query = entityManager.createQuery("Select r from ItemRequest r", ItemRequest.class);
        List<ItemRequest> requests = query.getResultList();
        assertThat(requests.get(0).getRequestId(), notNullValue());
        assertThat(requests.get(0).getDescription(), equalTo(requestDto.getDescription()));
        assertThat(requests.get(0).getRequestor(), equalTo(requestDto.getRequestor()));
        entityManager.clear();
    }

    @Test
    void getRequests() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(getUser.getId());
        itemService.addNewItem(getUser.getId(), item);
        requestDto.setRequestor(getUser.getId());
        itemRequestService.createRequest(requestDto, getUser.getId());
        Collection<ItemRequestDto> requests = itemRequestService.getRequests(getUser.getId());
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>(requests);
        assertThat(itemRequestDtoList.get(0).getId(), notNullValue());
        assertThat(itemRequestDtoList.get(0).getDescription(), equalTo(requestDto.getDescription()));
        assertThat(itemRequestDtoList.get(0).getRequestor(), equalTo(requestDto.getRequestor()));
        entityManager.clear();
    }

    @Test
    void getAll() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(getUser.getId());
        itemService.addNewItem(getUser.getId(), item);
        requestDto.setRequestor(getUser.getId());
        itemRequestService.createRequest(requestDto, getUser.getId());
        Collection<ItemRequestDto> requests = itemRequestService.getAllRequest(getUser.getId(), 1, 10);
        assertThat(requests.size(), equalTo(0));
        entityManager.clear();
    }

    @Test
    void getById() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(getUser.getId());
        itemService.addNewItem(getUser.getId(), item);
        requestDto.setRequestor(getUser.getId());
        ItemRequestDto getRequest = itemRequestService.createRequest(requestDto, getUser.getId());
        ItemRequestDto request = itemRequestService.getRequest(getRequest.getId(), getUser.getId());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        entityManager.clear();
    }

    @Test
    void addResponse() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(getUser.getId());
        itemService.addNewItem(getUser.getId(), item);
        requestDto.setRequestor(getUser.getId());
        ItemRequestDto getRequest = itemRequestService.createRequest(requestDto, getUser.getId());
        Item testItem = Item.builder()
                .id(item.getId() + 1)
                .name("test request")
                .description("test description")
                .ownerId(1L)
                .available(true)
                .requestId(getRequest.getId())
                .build();
        itemService.addNewItem(getUser.getId(), testItem);
        ItemRequestDto request = itemRequestService.getRequest(getRequest.getId(), getUser.getId());
        List<ItemDto> itemRequestDtoList = new ArrayList<>(request.getItems());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(request.getItems().size(), equalTo(1));
        assertThat(itemRequestDtoList.get(0).getName(), equalTo(testItem.getName()));
        entityManager.clear();
    }
}
