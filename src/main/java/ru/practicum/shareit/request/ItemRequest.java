package ru.practicum.shareit.request;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;
    @Column
    @NotBlank(message = "Описание не может быть пустым.")
    private String description;
    @Column(name = "requestor_id")
    private Long requestor;
    @Column(name = "created")
    private LocalDateTime created;

}
