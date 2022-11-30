package ru.practicum.shareit.jpatest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@DataJpaTest
public class ItemTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    private static Item item;
    private static User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .name("test")
                .email("test@test.ru")
                .build();
        item = Item.builder()
                .name("item")
                .description("description")
                .ownerId(1L)
                .available(true)
                .requestId(0L)
                .build();
    }

    @Test
    void load() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void save() {
        User user1 = userRepository.save(user);
        item.setOwnerId(user1.getId());
        Item item1 = itemRepository.save(item);

        Assertions.assertNotNull(item1.getId());
        Assertions.assertEquals(item1.getName(), "item");
    }

    @Test
    void search() {
        User user1 = userRepository.save(user);
        item.setOwnerId(user1.getId());
        itemRepository.save(item);
        Page<Item> items = itemRepository.search("ite", Pageable.unpaged());

        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.getSize(), 1);
    }


    @Test
    void findByOwner() {
        User user1 = userRepository.save(user);
        item.setOwnerId(user1.getId());
        itemRepository.save(item);
        Page<Long> items = itemRepository.findIdByOwner(user.getId(), Pageable.unpaged());

        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.getSize(), 1);
    }

    @Test
    void findByRequest() {
        User user1 = userRepository.save(user);
        item.setOwnerId(user1.getId());
        item.setRequestId(3L);
        itemRepository.save(item);
        Collection<Item> items = itemRepository.findByRequest_Id(3L);

        Assertions.assertNotNull(items);
        Assertions.assertEquals(items.size(), 1);
    }

    @Test
    void findById() {
        User user1 = userRepository.save(user);
        item.setOwnerId(user1.getId());
        Item item1 = itemRepository.save(item);
        Optional<Item> getItem = itemRepository.findById(item1.getId());

        Assertions.assertNotNull(getItem);
        Assertions.assertEquals(getItem.get().getDescription(), item1.getDescription());
    }

}
