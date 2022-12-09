package ru.practicum.shareit.unittests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
@SpringBootTest
public class BookingTests {
    private static UserDto userDto;
    private static UserDto booker;
    private static Item item;
    private static BookingDto bookingDto;
    private static Booking booking;
    private BookingService bookingService;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private BookingRepository mockBookingRepository;
    private static final LocalDateTime start = LocalDateTime.now();
    private static final LocalDateTime end = LocalDateTime.now().plusHours(1);

    @BeforeEach
    public void beforeEach() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("user@user.ru")
                .build();
        booker = UserDto.builder()
                .id(2L)
                .name("Test booker")
                .email("booker@booker.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("object")
                .description("Test object")
                .ownerId(1L)
                .available(true)
                .requestId(0L)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(UserMapper.dtoToUser(booker))
                .bookingStatus(BookingStatus.WAITING)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING)
                .build();
        bookingService = new BookingServiceImpl(mockBookingRepository, mockUserService, mockItemRepository);
        Mockito.when(mockUserService.getUser(Mockito.anyLong()))
                .thenReturn(userDto);
        Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

    }

    @Test
    void create() {
        Mockito.when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDtoWithUserAndItem dto = bookingService.createBooking(bookingDto, 2L);
        Assertions.assertEquals(dto.getId(), booking.getId());
        Assertions.assertEquals(dto.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(dto.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(dto.getStart(), booking.getStart());
        Assertions.assertEquals(dto.getEnd(), booking.getEnd());

    }

    @Test
    void createUnCorrectDate() {
        bookingDto.setEnd(start.minusMonths(1));
        Assertions.assertThrows(
                ValidationException.class, () -> bookingService.createBooking(bookingDto, 2L));
    }

    @Test
    void createOwnerWantBooking() {
        Assertions.assertThrows(
                NotFoundException.class, () -> bookingService.createBooking(bookingDto, 1L));

    }

    @Test
    void createNotAvailable() {
        item.setAvailable(false);
        Assertions.assertThrows(
                ValidationException.class, () -> bookingService.createBooking(bookingDto, 2L));
    }


    @Test
    void update() {
        Mockito.when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDtoWithUserAndItem dto = bookingService.approvalBooking(true, 1L, 1L);
        Assertions.assertEquals(dto.getId(), booking.getId());
    }

    @Test
    void updateWithUnCorrectOwner() {
        Assertions.assertThrows(
                NotFoundException.class, () -> bookingService.approvalBooking(true, 2L, 1L));
    }


    @Test
    void updateNotFoundBooking() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(
                NotFoundException.class, () -> bookingService.approvalBooking(true, 1L, 1L));
    }

    @Test
    void updateRepeat() {
        booking.setBookingStatus(BookingStatus.APPROVED);
        Assertions.assertThrows(
                ValidationException.class, () -> bookingService.approvalBooking(true, 1L, 1L));
    }

    @Test
    void findAllByIdNoAccess() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(3L, 1L));
    }

    @ParameterizedTest
    @ValueSource(strings = {"WAITING", "REJECTED", "PAST", "FUTURE", "ALL"})
    void findAllById(String status) {
        Mockito.when(mockUserService.getUser(anyLong()))
                .thenReturn(booker);
        Mockito.when(mockBookingRepository.findAllByBooker_Id(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(mockBookingRepository.findAllByBooker_IdAndEndBefore(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(mockBookingRepository.findAllByBooker_IdAndStartAfter(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(mockBookingRepository.findAllByBooker_IdAndBookingStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(mockBookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        Collection<BookingDtoWithUserAndItem> dtos = bookingService.getBookingsUser(status, 2L, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"WAITING", "REJECTED", "PAST", "FUTURE", "ALL"})
    void findAllByOwner(String status) {
        Mockito.when(mockBookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(mockBookingRepository.findAllByOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(mockBookingRepository.findAllByOwnerIdAndBookingStatus(anyLong(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(mockBookingRepository.findAllByOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(mockBookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        Collection<BookingDtoWithUserAndItem> dtos = bookingService.getBookingsItemOwner(status, 1L, 1, 10);
        Assertions.assertEquals(dtos.size(), 1);
    }

    @Test
    void mapperToBooking() {
        Booking getBooking = BookingMapper.toBooking(bookingDto);
        Assertions.assertEquals(getBooking.getId(), bookingDto.getId());

    }

    @Test
    void mapperToBookingDto() {
        BookingDto getBookingDto = BookingMapper.toBookingDto(booking);
        Assertions.assertEquals(getBookingDto.getId(), booking.getId());

    }

    @Test
    void mapperToBookingDtoWithUserAndItem() {
        BookingDtoWithUserAndItem getBooking = BookingMapper.toBookingDtoWithUserAndItem(booking);
        Assertions.assertEquals(getBooking.getId(), booking.getId());
    }

    @Test
    void bookingDtoWithUserAndItemTestGet() {
        BookingDtoWithUserAndItem bookingDtoWithUserAndItem = new BookingDtoWithUserAndItem();
        Assertions.assertNull(bookingDtoWithUserAndItem.getId());
        Assertions.assertNull(bookingDtoWithUserAndItem.getItem());
        Assertions.assertNull(bookingDtoWithUserAndItem.getBooker());
        Assertions.assertNull(bookingDtoWithUserAndItem.getStatus());
        Assertions.assertNull(bookingDtoWithUserAndItem.getStart());
        Assertions.assertNull(bookingDtoWithUserAndItem.getEnd());

    }

    @Test
    void bookingDtoWithUserAndItemTestSet() {
        BookingDtoWithUserAndItem bookingDtoWithUserAndItem = new BookingDtoWithUserAndItem();
        bookingDtoWithUserAndItem.setId(1L);
        bookingDtoWithUserAndItem.setStatus(BookingStatus.WAITING);
        bookingDtoWithUserAndItem.setItem(item);
        bookingDtoWithUserAndItem.setBooker(UserMapper.dtoToUser(booker));

        Assertions.assertEquals(bookingDtoWithUserAndItem.getId(), 1L);
        Assertions.assertEquals(bookingDtoWithUserAndItem.getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(bookingDtoWithUserAndItem.getItem().getName(), item.getName());
        Assertions.assertEquals(bookingDtoWithUserAndItem.getBooker().getName(), booker.getName());

    }

}
