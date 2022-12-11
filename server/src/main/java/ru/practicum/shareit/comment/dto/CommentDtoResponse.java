package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoResponse {
    private Long id;
    private String text;
    @JsonProperty("authorName")
    private String author;
    private LocalDateTime created;
}
