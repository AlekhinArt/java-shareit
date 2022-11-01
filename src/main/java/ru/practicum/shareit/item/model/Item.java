package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    @Positive
    private Long id;
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание не может быть пустым.")
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;


}
