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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

public class ItemTest {

    private final EntityManager entityManager;
    private final ItemService itemService;
    private static Item item;
    private static ItemDto itemDto;

    private final UserService userService;
    private static User user;

    @BeforeEach
    @Sql({"/schema.sql"})
    public void beforeEach() {
        item = Item.builder()
                .id(1L)
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

    }

    @AfterEach
    @Sql({"/clean.sql"})
    void clean() {
    }

    @Test
    void saveItem() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(user.getId());
        itemService.addNewItem(getUser.getId(), item);

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void updateItem() {
        UserDto getUser = userService.createUser(user);
        itemDto.setOwnerId(user.getId());
        ItemDto testItem = itemService.addNewItem(getUser.getId(), item);
        item.setDescription("new description");
        itemService.updateItem(item, getUser.getId(), testItem.getId());

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item2 = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(item2.getName()));
        assertThat(item.getDescription(), equalTo(item2.getDescription()));
    }

    @Test
    void findById() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(user.getId());
        ItemDto testItem = itemService.addNewItem(getUser.getId(), item);

        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", testItem.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(testItem.getName()));
        assertThat(item.getDescription(), equalTo(testItem.getDescription()));
    }

    @Test
    void findAllById() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(user.getId());
        ItemDto testItem = itemService.addNewItem(getUser.getId(), item);
        item.setId(testItem.getId() + 1);
        itemService.addNewItem(getUser.getId(), item);
        Collection<ItemDto> items = itemService.getItemsCreator(getUser.getId(), 1, 10);
        assertThat(items.size(), equalTo(2));
    }

    @Test
    void search() {
        UserDto getUser = userService.createUser(user);
        item.setOwnerId(user.getId());
        itemService.addNewItem(user.getId(), item);
        Collection<ItemDto> items = itemService.findItem("t", getUser.getId(), 1, 10);
        assertThat(items.size(), equalTo(1));
        assertThat(new ArrayList<>(items).get(0).getName(), equalTo(item.getName()));
    }
}
