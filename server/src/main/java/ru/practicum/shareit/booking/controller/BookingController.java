package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoWithUserAndItem createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestBody BookingDto booking) {
        log.info("Creating booking {}, userId={}", booking, userId);
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoWithUserAndItem approvalBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam("approved") boolean approved,
                                                     @PathVariable("bookingId") long bookingId) {
        log.info("Updating booking {}, userId={}", bookingId, userId);
        return bookingService.approvalBooking(approved, userId, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoWithUserAndItem getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable("bookingId") long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoWithUserAndItem> getBookingsUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                 @RequestParam(required = false,
                                                                         defaultValue = "ALL") String state,
                                                                 @RequestParam(required = false,
                                                                         defaultValue = "0") int from,
                                                                 @RequestParam(required = false,
                                                                         defaultValue = "10") int size) {
        log.info("getBookingsUser with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingService.getBookingsUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoWithUserAndItem> getBookingsItemOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                                      @RequestParam(required = false,
                                                                              defaultValue = "ALL") String state,
                                                                      @RequestParam(required = false,
                                                                              defaultValue = "0") int from,
                                                                      @RequestParam(required = false,
                                                                              defaultValue = "10") int size) throws UnsupportedStatusException {
        log.info("getBookingsItemOwner with state {}, ownerId={}, from={}, size={}", state, ownerId, from, size);
        return bookingService.getBookingsItemOwner(state, ownerId, from, size);
    }
}
