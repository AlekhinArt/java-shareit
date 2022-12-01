package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private static final CommentDto commentDto = new CommentDto("comment");

    @Test
    void test() throws IOException {
        var result = json.write(commentDto);
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
    }
}
