package ru.practicum.shareit.request.dto;

import java.util.Collection;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemRequestDto {

    private Long id;
    @NonNull
    private String description;

    private Long requestor;

    private LocalDateTime created;

    private Collection<ItemDto> items;


}
