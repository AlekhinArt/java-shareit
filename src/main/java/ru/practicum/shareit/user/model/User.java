package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive
    @Column(name = "user_id")
    private Long id;
    @Column(name = "user_name")
    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Логин не может содержать пробелы.")
    private String name;
    @Column
    @NotBlank(message = "Электронная почта не может быть пустой.")
    @Email(regexp = "\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}",
            message = "Электронная почта должна соответсвовать формату RFC 5322.")
    private String email;


}
