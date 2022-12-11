package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoWithUserAndItem createBooking(BookingDto bookingDto, Long userId) {
        Item item = getItem(bookingDto.getItemId());
        User user = getUser(userId);
        if (userId.equals(item.getOwnerId())) {
            log.debug("createBooking.NotFoundException.Нельзя забронировать свою вещь");
            throw new NotFoundException("Ошибка! Нельзя забронировать свою вещь!");
        }
        if (!item.getAvailable()) {
            log.debug("createBooking.ValidationException.Вещь не доступна для броинрования");
            throw new ValidationException("Вещь не доступна для броинрования");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            log.debug("createBooking.ValidationException.Не корректное время");
            throw new ValidationException("Время окончания бронирования должно быть позже времени начала бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBookingStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        log.info("createBooking booking: {}", booking);
        return BookingMapper.toBookingDtoWithUserAndItem(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoWithUserAndItem approvalBooking(Boolean approved, Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        Item item = booking.getItem();
        User user = getUser(userId);
        if (!item.getOwnerId().equals(userId)) {
            log.debug("approvalBooking.NotFoundException.Подтверждать статус может только создатель бронирования");
            throw new NotFoundException("Подтверждать статус может только создатель бронирования");
        }
        if (booking.getBookingStatus() == BookingStatus.APPROVED) {
            log.debug("approvalBooking.NotFoundException.Бронирование уже подтверждено");
            throw new ValidationException("Бронирование уже подтверждено");
        }
        booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("approvalBooking booking: {}", booking);
        return BookingMapper.toBookingDtoWithUserAndItem(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoWithUserAndItem getBooking(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        Item item = booking.getItem();
        User user = booking.getBooker();
        if (!user.getId().equals(userId) && !item.getOwnerId().equals(userId)) {
            log.debug("getBooking.NotFoundException.Нет доступа");
            throw new NotFoundException("Нет доступа");
        }
        log.info("getBooking userId: {}, bookingId: {}", userId, bookingId);
        return BookingMapper.toBookingDtoWithUserAndItem(booking);
    }

    @Override
    public Collection<BookingDtoWithUserAndItem> getBookingsUser(String stateString, Long userId, int from, int size) {
        if (getUser(userId).getId() == null) {
            log.debug("getBookingsUser.NotFoundException.Такого пользователя нет");
            throw new NotFoundException("Такого пользователя нет");
        }
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
        log.info("getBookingsUser stateString: {}, userId: {}, from: {}, size: {}", state, userId, from, size);
        return bookings.stream()
                .map(BookingMapper::toBookingDtoWithUserAndItem)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDtoWithUserAndItem> getBookingsItemOwner(String stateString, Long ownerId, int from, int size) {
        if (getUser(ownerId).getId() == null) {
            log.debug("getBookingsUser.NotFoundException.Такого пользователя нет");
            throw new NotFoundException("Такого пользователя нет");
        }
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
        log.info("getBookingsItemOwner stateString: {}, ownerId: {}, from: {}, size: {}", state, ownerId, from, size);
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
