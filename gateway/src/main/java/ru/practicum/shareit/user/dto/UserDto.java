package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class UserDto {
    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Логин не может содержать пробелы.")
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой.")
    @Email(regexp = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}",
            message = "Электронная почта должна соответсвовать формату RFC 5322.")
    private String email;
}

