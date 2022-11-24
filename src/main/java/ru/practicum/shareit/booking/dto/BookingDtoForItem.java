package ru.practicum.shareit.booking.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BookingDtoForItem {
    private Long id;
    private Long bookerId;

}
