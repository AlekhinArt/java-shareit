package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.valid.Create;
import ru.practicum.shareit.valid.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class UserDto {
    @NotBlank(message = "Логин не может быть пустым.", groups = {Create.class})
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Логин не может содержать пробелы.",
            groups = {Create.class, Update.class})
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой.",
            groups = {Create.class})
    @Email(regexp = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}",
            message = "Электронная почта должна соответсвовать формату RFC 5322.",
            groups = {Create.class, Update.class})
    private String email;
}

