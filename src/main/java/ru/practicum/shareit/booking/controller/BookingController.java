package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoWithUserAndItem createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @Valid @RequestBody BookingDto booking) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoWithUserAndItem approvalBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam("approved") boolean approved,
                                                     @PathVariable("bookingId") long bookingId) {
        return bookingService.approvalBooking(approved, userId, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoWithUserAndItem getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable("bookingId") long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoWithUserAndItem> getBookingsUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                 @RequestParam(required = false, defaultValue = "ALL")
                                                                 String state) throws UnsupportedStatusException {
        return bookingService.getBookingsUser(state, userId);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoWithUserAndItem> getBookingsItemOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                                      @RequestParam(required = false, defaultValue = "ALL")
                                                                      String state) throws UnsupportedStatusException {
        return bookingService.getBookingsItemOwner(state, ownerId);
    }
}
