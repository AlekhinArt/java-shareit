package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoWithUserAndItem createBooking(BookingDto bookingDto, Long userId) {
        System.out.println();
        Item item = getItem(bookingDto.getItemId());
        User user = getUser(userId);
        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException("Ошибка! Нельзя забронировать свою вещь!");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для броинрования");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Время окончания бронирования должно быть позже времени начала бронирования");
        }
        Booking booking = BookingMapper.toBookingDto(bookingDto);
        booking.setBookingStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return BookingMapper.toBookingDtoWithUserAndItem(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoWithUserAndItem approvalBooking(Boolean approved, Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        Item item = booking.getItem();
        User user = getUser(userId);
        if (!item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Подтверждать статус может только создатель бронирования");
        }
        if (booking.getBookingStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDtoWithUserAndItem(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoWithUserAndItem getBooking(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        Item item = booking.getItem();
        User user = booking.getBooker();
        if (!user.getId().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Нет доступа");
        }
        return BookingMapper.toBookingDtoWithUserAndItem(booking);
    }

    @Override
    public Collection<BookingDtoWithUserAndItem> getBookingsUser(String stateString, Long userId) {
        if (getUser(userId).getId() == null) throw new NotFoundException("Такого пользователя нет");
        if (Arrays.stream(BookingState.values()).noneMatch((t) -> t.name().equals(stateString))) {
            throw new UnsupportedStatusException(stateString);
        }
        BookingState state = BookingState.valueOf(stateString);
        Collection<Booking> bookings;
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, time);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, time);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, time, time);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findAllByBooker_IdAndBookingStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findAllByBooker_IdAndBookingStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new UnsupportedStatusException(state.toString());

        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithUserAndItem)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDtoWithUserAndItem> getBookingsItemOwner(String stateString, Long ownerId) {
        if (getUser(ownerId).getId() == null) throw new NotFoundException("Такого пользователя нет");
        if (Arrays.stream(BookingState.values()).noneMatch((t) -> t.name().equals(stateString))) {
            throw new UnsupportedStatusException(stateString);
        }
        Collection<Booking> bookings;
        LocalDateTime time = LocalDateTime.now();
        BookingState state = BookingState.valueOf(stateString);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerIdOrderByStartDesc(ownerId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndEndBeforeOrderByStartDesc(ownerId, time);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStartAfterOrderByStartDesc(ownerId, time);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, time, time);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findAllByOwnerIdAndBookingStatusOrderByStartDesc(ownerId, BookingStatus.WAITING.toString());
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findAllByOwnerIdAndBookingStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED.toString());
                break;
            default:
                throw new UnsupportedStatusException(state.toString());
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithUserAndItem)
                .collect(Collectors.toList());

    }

    private User getUser(long userId) {
        userService.getUser(userId);

        return UserMapper.dtoToUser(userService.getUser(userId));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такого предмета нет"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }
}
