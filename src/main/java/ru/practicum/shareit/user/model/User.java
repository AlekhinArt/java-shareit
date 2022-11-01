package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
public class User {
    @Positive
    private Long id;
    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Логин не может содержать пробелы.")
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой.")
    @Email(regexp = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}",
            message = "Электронная почта должна соответсвовать формату RFC 5322.")
    private String email;

}
