package ru.practicum.shareit.unittests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest
public class ItemTest {
    private ItemService itemService;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;

    private static User user = User.builder()
            .id(1L)
            .name("Test")
            .email("test@test.ru")
            .build();
    private static Item item = Item.builder()
            .id(1L)
            .name("testItem")
            .description("test item description")
            .ownerId(1L)
            .available(true)
            .requestId(0L)
            .build();
    private static ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("testItem")
            .description("test item description")
            .ownerId(1L)
            .available(true)
            .requestId(0L)
            .build();
    private static Booking booking = Booking.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1))
            .item(new Item())
            .booker(new User())
            .bookingStatus(BookingStatus.APPROVED)
            .build();

    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImpl(mockUserRepository, mockItemRepository,
                mockBookingRepository, mockCommentRepository);
    }

    @Test
    void createOk() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto getItem = itemService.addNewItem(1, item);
        Assertions.assertEquals(getItem.getName(), item.getName());
    }

    @Test
    void createUserNotFound() {
        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.addNewItem(1, item));
    }

    @Test
    void updateOk() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto getItem = itemService.updateItem(item, 1L, 1L);
        Assertions.assertEquals(getItem.getName(), itemDto.getName());
    }

    @Test
    void updateUserNotFound() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(item, 1L, 1L));
    }

    @Test
    void updateItemNotFound() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(item, 1L, 1L));

    }

    @Test
    void updateEditNotOwner() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockItemRepository.save(any(Item.class)))
                .thenReturn(item);
        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(item, 2L, 1L));
    }

    @Test
    void findAllByIdOk() {
        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Pageable pageable = PageRequest.of(0, 20);
        Mockito.when(mockItemRepository.findIdByOwner(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(1L)));
        Mockito.when(mockItemRepository.getByIdForResponse(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemDto);
        Assertions.assertEquals(itemService.getItemsCreator(1L, 1, 20).size(), 1);
    }

    @Test
    void findAllByIdUserNotFound() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItemsCreator(1, 1, 20));

    }

    @Test
    void findAllByIdPageParametersFail() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getItemsCreator(1, -1, 20));

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getItemsCreator(1, 1, 0));

    }

    @Test
    void searchOk() {
        Pageable pageable = PageRequest.of(0, 20);
        Mockito
                .when(mockItemRepository.search("test", pageable))
                .thenReturn(new PageImpl<>(List.of(item)));
        Assertions.assertEquals(itemService.findItem("test", 1, 0, 20).size(), 1);
    }

    @Test
    void searchEmptyText() {
        Assertions.assertEquals(itemService.findItem("", 1, 0, 20).size(), 0);
    }

    @Test
    void searchPageParametersFail() {
        Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.findItem("32", 1L, 0, 0));
        Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.findItem("23", 1L, -1, 20));

    }

    @Test
    void addCommentOk() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(mockBookingRepository.findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        Mockito
                .when(mockCommentRepository.save(new Comment()))
                .thenReturn(new Comment());
        itemService.addComment(1, 1, new CommentDto("комментарий"));
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class));
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito.verify(mockUserRepository, Mockito.times(2))
                .findById(Mockito.anyLong());
        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .save(any(Comment.class));
    }

    @Test
    void addCommentNotBooking() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(mockBookingRepository.findAllByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.addComment(1, 1, new CommentDto("комментарий")));

    }


    @Test
    void mapperToItemDto() {
        ItemDto getItemDto = ItemMapper.toItemDto(item);
        Assertions.assertEquals(getItemDto.getName(), item.getName());

    }


}
