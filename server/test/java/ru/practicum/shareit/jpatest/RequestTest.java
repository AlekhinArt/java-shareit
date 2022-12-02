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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@DataJpaTest
public class RequestTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    private static Item item;
    private static User owner;
    private static User user;
    private static ItemRequest itemRequest;
    private static final LocalDateTime created = LocalDateTime.now();

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

        owner = User.builder()
                .name("owner")
                .email("owner@owner.ru")
                .build();


        itemRequest = ItemRequest.builder()
                .description("description")
                .created(created)
                .build();
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void saveRequest() {
        userRepository.save(owner);
        userRepository.save(user);
        itemRequest.setRequestor(user.getId());
        itemRequestRepository.save(itemRequest);

        Assertions.assertNotNull(itemRequest.getRequestId());
        Assertions.assertEquals(itemRequest.getCreated(), created);
        Assertions.assertEquals(itemRequest.getDescription(), "description");
        Assertions.assertEquals(itemRequest.getRequestor(), user.getId());
    }

    @Test
    void findByRequestor() {
        User owner1 = userRepository.save(owner);
        User user1 = userRepository.save(user);
        itemRequest.setRequestor(user1.getId());
        itemRequestRepository.save(itemRequest);

        item.setOwnerId(owner1.getId());
        itemRepository.save(item);
        Collection<ItemRequest> requests = itemRequestRepository.findByRequestor(user1.getId());

        Assertions.assertNotNull(requests);
        Assertions.assertEquals(requests.size(), 1);
        Assertions.assertEquals(new ArrayList<>(requests).get(0).getDescription(), "description");
    }

    @Test
    void findAllByRequestorNot() {
        User owner1 = userRepository.save(owner);
        User user1 = userRepository.save(user);
        itemRequest.setRequestId(user1.getId());
        itemRequestRepository.save(itemRequest);
        item.setOwnerId(owner1.getId());
        itemRepository.save(item);
        Page<ItemRequest> requests = itemRequestRepository.findAllByRequestorNot(user.getId(), Pageable.unpaged());
        Assertions.assertEquals(requests.getSize(), 0);
    }

}
