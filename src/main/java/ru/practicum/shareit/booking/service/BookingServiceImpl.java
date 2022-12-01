package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoWithUserAndItem createBooking(BookingDto bookingDto, Long userId) {
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
        Booking booking = BookingMapper.toBooking(bookingDto);
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
    public Collection<BookingDtoWithUserAndItem> getBookingsUser(String stateString, Long userId, int from, int size) {
        if (getUser(userId).getId() == null) throw new NotFoundException("Такого пользователя нет");
        if (Arrays.stream(BookingState.values()).noneMatch((t) -> t.name().equals(stateString))) {
            throw new UnsupportedStatusException(stateString);
        }

        checkPageParam(from, size);

        BookingState state = BookingState.valueOf(stateString);
        Page<Booking> bookings;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBooker_IdAndEndBefore(userId, time, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBooker_IdAndStartAfter(userId, time, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(userId, time, time, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndBookingStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndBookingStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new UnsupportedStatusException(state.toString());

        }
        if (bookings == null || bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithUserAndItem)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDtoWithUserAndItem> getBookingsItemOwner(String stateString, Long ownerId, int from, int size) {
        if (getUser(ownerId).getId() == null) throw new NotFoundException("Такого пользователя нет");
        if (Arrays.stream(BookingState.values()).noneMatch((t) -> t.name().equals(stateString))) {
            throw new UnsupportedStatusException(stateString);
        }
        checkPageParam(from, size);
        Page<Booking> bookings;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime time = LocalDateTime.now();
        BookingState state = BookingState.valueOf(stateString);
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(ownerId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, time, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, time, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(ownerId, time, time, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndBookingStatus(ownerId, BookingStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndBookingStatus(ownerId, BookingStatus.REJECTED,
                        pageable);
                break;
            default:
                throw new UnsupportedStatusException(state.toString());
        }
        if (bookings == null || bookings.isEmpty()) {
            return Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithUserAndItem)
                .collect(Collectors.toList());

    }

    private void checkPageParam(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Индекс первого элемента должен быть больше 0");
        }
        if (size <= 0) {
            throw new ValidationException("Количество предметов должно быть больше 0");
        }
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
