package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemRequestDto {

    private Long id;
    @NotBlank(message = "Описание не может быть пустым.")
    private String description;

    private Long requestor;

    private LocalDateTime created;

    private Collection<ItemDto> items;
}
