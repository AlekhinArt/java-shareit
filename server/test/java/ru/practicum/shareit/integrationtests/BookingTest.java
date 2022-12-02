package ru.practicum.shareit.integrationtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test_shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

public class BookingTest {
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private static Item item;
    private static User owner;
    private static User booker;
    private static BookingDto booking;
    private static final LocalDateTime start = LocalDateTime.now();
    private static final LocalDateTime end = LocalDateTime.now().plusHours(2);

    @BeforeEach
    @Sql({"/schema.sql"})
    public void beforeEach() {
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("test description")
                .ownerId(1L)
                .available(true)
                .requestId(0L)
                .build();
        owner = User.builder()
                .name("owner")
                .email("owner@test.ru")
                .build();
        booker = User.builder()
                .name("booker")
                .email("booker@test.ru")
                .build();
        booking = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .bookerId(2L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();
    }

    @AfterEach
    @Sql({"/clean.sql"})
    void clean() {
    }

    @Test
    void createBooking() {
        UserDto ownerDto = userService.createUser(owner);
        UserDto bookerDto = userService.createUser(booker);
        item.setOwnerId(ownerDto.getId());
        ItemDto getItem = itemService.addNewItem(ownerDto.getId(), item);
        booking.setBookerId(bookerDto.getId());
        booking.setItemId(getItem.getId());
        bookingService.createBooking(booking, bookerDto.getId());

        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookerDto.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBookingStatus(), equalTo(BookingTest.booking.getStatus()));
        assertThat(booking.getStart(), equalTo(BookingTest.booking.getStart()));
    }

    @Test
    void approvalBooking() {
        UserDto ownerDto = userService.createUser(owner);
        UserDto bookerDto = userService.createUser(booker);
        item.setOwnerId(ownerDto.getId());
        ItemDto getItem = itemService.addNewItem(ownerDto.getId(), item);
        booking.setBookerId(bookerDto.getId());
        booking.setItemId(getItem.getId());
        BookingDtoWithUserAndItem getBooking = bookingService.createBooking(booking, bookerDto.getId());
        bookingService.approvalBooking(true, ownerDto.getId(), getBooking.getId());

        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookerDto.getId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBookingStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(booking.getStart(), equalTo(BookingTest.booking.getStart()));
    }

    @Test
    void getBooking() {
        UserDto ownerDto = userService.createUser(owner);
        UserDto bookerDto = userService.createUser(booker);
        item.setOwnerId(ownerDto.getId());
        ItemDto getItem = itemService.addNewItem(ownerDto.getId(), item);
        booking.setBookerId(bookerDto.getId());
        booking.setItemId(getItem.getId());
        BookingDtoWithUserAndItem getBooking = bookingService.createBooking(booking, bookerDto.getId());
        BookingDtoWithUserAndItem booking = bookingService.getBooking(ownerDto.getId(), getBooking.getId());

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(booking.getStatus()));
        assertThat(booking.getStart(), equalTo(booking.getStart()));
        assertThat(booking.getItem().getName(), equalTo(item.getName()));
        assertThat(booking.getBooker().getName(), equalTo(booker.getName()));
    }

    @Test
    void getBookingsUser() {
        UserDto ownerDto = userService.createUser(owner);
        UserDto bookerDto = userService.createUser(booker);
        item.setOwnerId(ownerDto.getId());
        ItemDto getItem = itemService.addNewItem(ownerDto.getId(), item);
        booking.setBookerId(bookerDto.getId());
        booking.setItemId(getItem.getId());
        bookingService.createBooking(booking, bookerDto.getId());

        Collection<BookingDtoWithUserAndItem> bookings = bookingService.getBookingsUser(BookingState.ALL.name(), bookerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(1));
        bookings = bookingService.getBookingsUser(BookingState.WAITING.name(), bookerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(1));
        bookings = bookingService.getBookingsUser(BookingState.REJECTED.name(), bookerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(0));
        bookings = bookingService.getBookingsUser(BookingState.FUTURE.name(), bookerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(0));
        bookings = bookingService.getBookingsUser(BookingState.PAST.name(), bookerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(0));
        bookings = bookingService.getBookingsUser(BookingState.CURRENT.name(), bookerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void getBookingsItemOwner() {
        UserDto ownerDto = userService.createUser(owner);
        UserDto bookerDto = userService.createUser(booker);
        item.setOwnerId(ownerDto.getId());
        ItemDto getItem = itemService.addNewItem(ownerDto.getId(), item);
        booking.setBookerId(bookerDto.getId());
        booking.setItemId(getItem.getId());
        bookingService.createBooking(booking, bookerDto.getId());


        Collection<BookingDtoWithUserAndItem> bookings = bookingService.getBookingsItemOwner(BookingState.ALL.name(),
                ownerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(1));

        bookings = bookingService.getBookingsItemOwner(BookingState.WAITING.name(), ownerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(1));
        bookings = bookingService.getBookingsItemOwner(BookingState.REJECTED.name(), ownerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(0));
        bookings = bookingService.getBookingsItemOwner(BookingState.FUTURE.name(), ownerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(0));
        bookings = bookingService.getBookingsItemOwner(BookingState.PAST.name(), ownerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(0));
        bookings = bookingService.getBookingsItemOwner(BookingState.CURRENT.name(), ownerDto.getId(), 1, 10);
        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void getBookingsUserUnSupportedStatus() {
        UserDto ownerDto = userService.createUser(owner);
        UserDto bookerDto = userService.createUser(booker);
        item.setOwnerId(ownerDto.getId());
        ItemDto getItem = itemService.addNewItem(ownerDto.getId(), item);
        booking.setBookerId(bookerDto.getId());
        booking.setItemId(getItem.getId());
        bookingService.createBooking(booking, bookerDto.getId());

        final UnsupportedStatusException exception = Assertions.assertThrows(
                UnsupportedStatusException.class,
                () -> bookingService.getBookingsUser("WEWE", bookerDto.getId(), 1, 10));
    }

}
