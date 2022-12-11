package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;

import java.util.Collection;

public interface BookingService {

    BookingDtoWithUserAndItem createBooking(BookingDto booking, Long userId);

    BookingDtoWithUserAndItem approvalBooking(Boolean approved, Long userId, Long bookingId);

    BookingDtoWithUserAndItem getBooking(Long userId, Long bookingId);

    Collection<BookingDtoWithUserAndItem> getBookingsUser(String state, Long userId, int from, int size);

    Collection<BookingDtoWithUserAndItem> getBookingsItemOwner(String state, Long ownerId, int from, int size);
}
