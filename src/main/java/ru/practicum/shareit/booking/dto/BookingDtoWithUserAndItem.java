package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoWithUserAndItem {

    private long id;
    @Future
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
