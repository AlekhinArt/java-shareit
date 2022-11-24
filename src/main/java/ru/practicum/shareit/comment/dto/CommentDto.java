package ru.practicum.shareit.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @NotBlank(message = "Нельзя оставлять пустой комментарий")
    private String text;
}
