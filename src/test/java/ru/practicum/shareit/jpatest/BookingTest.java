package ru.practicum.shareit.jpatest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
public class BookingTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;
    private static Item item;
    private static Booking booking;
    private static User owner;
    private static User booker;
    private static User testOwner;
    private static User testBooker;
    private static Item testItem;
    private static final LocalDateTime start = LocalDateTime.now();
    private static final LocalDateTime end = LocalDateTime.now().plusHours(1);

    @BeforeEach
    void beforeEach() {
        owner = User.builder()
                .name("owner")
                .email("owner@owner.ru")
                .build();
        item = Item.builder()
                .name("item")
                .description("description")
                .ownerId(1L)
                .available(true)
                .requestId(0L)
                .build();
        booker = User.builder()
                .name("booker")
                .email("booker@booker.ru")
                .build();

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(start)
                .end(end)
                .bookingStatus(BookingStatus.WAITING)
                .build();
        testOwner = userRepository.save(owner);
        testBooker = userRepository.save(booker);
        item.setOwnerId(testOwner.getId());
        testItem = itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void saveItem() {
        Assertions.assertNotNull(booking.getId());
        Assertions.assertEquals(booking.getStart(), start);
        Assertions.assertEquals(booking.getBookingStatus(), BookingStatus.WAITING);
    }

    @Test
    void findAllByBooker_Id() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_Id(booker.getId(), Pageable.unpaged());
        Assertions.assertNotNull(bookings);
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBooker_IdAndEndBefore() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_IdAndEndBefore(testBooker.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = bookingRepository.findAllByBooker_IdAndEndBefore(testBooker.getId(), LocalDateTime.now().plusHours(2), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBooker_IdAndStartAfter() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_IdAndStartAfter(testBooker.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = bookingRepository.findAllByBooker_IdAndStartAfter(testBooker.getId(), LocalDateTime.now().minusHours(2), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBooker_IdAndStartBeforeAndEndAfter() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(testBooker.getId(), LocalDateTime.now().plusMinutes(2), LocalDateTime.now().plusMinutes(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByBooker_IdAndStatus() {
        Page<Booking> bookings = bookingRepository.findAllByBooker_IdAndBookingStatus(testBooker.getId(), BookingStatus.REJECTED, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = bookingRepository.findAllByBooker_IdAndBookingStatus(testBooker.getId(), BookingStatus.WAITING, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByOwnerId() {
        Page<Booking> bookings = bookingRepository.findAllByOwnerId(testOwner.getId(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);

        bookings = bookingRepository.findAllByOwnerId(testBooker.getId(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);
    }

    @Test
    void findAllByOwnerIdAndEndBefore() {
        Page<Booking> bookings = bookingRepository.findAllByOwnerIdAndEndBefore(testOwner.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = bookingRepository.findAllByOwnerIdAndEndBefore(testOwner.getId(), LocalDateTime.now().plusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItem_IdInAndStartAfter() {
        Page<Booking> bookings = bookingRepository.findAllByOwnerIdAndStartAfter(testOwner.getId(), LocalDateTime.now(), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = bookingRepository.findAllByOwnerIdAndStartAfter(testOwner.getId(), LocalDateTime.now().minusHours(3), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByItem_IdInAndStartBeforeAndEndAfter() {
        Page<Booking> bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(testOwner.getId(), LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(5), Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void findAllByOwnerIdAndBookingStatus() {
        Page<Booking> bookings = bookingRepository.findAllByOwnerIdAndBookingStatus(testOwner.getId(), BookingStatus.REJECTED, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 0);

        bookings = bookingRepository.findAllByOwnerIdAndBookingStatus(testOwner.getId(), BookingStatus.WAITING, Pageable.unpaged());
        Assertions.assertEquals(bookings.getSize(), 1);
    }

    @Test
    void getByIdForResponse() {
        Comment comment = Comment.builder()
                .text("comment")
                .item(testItem)
                .created(LocalDateTime.now())
                .author(testBooker)
                .build();
        commentRepository.save(comment);

        ItemDto itemDto = itemRepository.getByIdForResponse(testBooker.getId(), testItem.getId());
        Assertions.assertNotNull(itemDto);
        Assertions.assertEquals(itemDto.getComments().size(), 1);
    }

}
