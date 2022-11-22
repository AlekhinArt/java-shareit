package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoResponse {
    private long id;
    private String text;
    @JsonProperty("authorName")
    private String author;
    private LocalDateTime created;
}
