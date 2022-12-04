package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comment.dto.CommentDtoResponse;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentResponseTests {
    @Autowired
    private JacksonTester<CommentDtoResponse> json;
    private static final LocalDateTime now = LocalDateTime.now();
    private static final CommentDtoResponse commentDto = CommentDtoResponse.builder()
            .id(1L)
            .text("comment")
            .author("author")
            .created(now)
            .build();

    @Test
    void test() throws IOException {
        var result = json.write(commentDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthor());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
    }
}
