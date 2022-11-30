package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    @Column(name = "item_name")
    @NotBlank(message = "Имя не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание не может быть пустым.")
    @Column
    private String description;
    @Column
    @NotNull
    private Boolean available;
    @Column(name = "owner_id")
    @JsonProperty("owner")
    private Long ownerId;
    @Column(name = "request_id")
    private Long requestId;

}
