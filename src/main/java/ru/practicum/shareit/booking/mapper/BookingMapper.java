package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {

        return BookingDto.builder()
                .id((booking.getId()))
                .bookerId(booking.getBooker().getId())
                .itemId(booking.getItem().getId())
                .itemName(booking.getItem().getName())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getBookingStatus())
                .build();
    }

    public static Booking toBookingDto(BookingDto bookingDto) {

        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .bookingStatus(bookingDto.getStatus())
                .build();
    }

    public static BookingDtoWithUserAndItem toBookingDtoWithUserAndItem(Booking booking) {

        return BookingDtoWithUserAndItem.builder()
                .id((booking.getId()))
                .booker(booking.getBooker())
                .item(booking.getItem())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getBookingStatus())
                .build();
    }


}
