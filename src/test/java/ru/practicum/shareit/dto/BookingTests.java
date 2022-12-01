package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingTests {
    @Autowired
    private JacksonTester<BookingDto> json;
    private static final LocalDateTime now = LocalDateTime.now();
    private static final LocalDateTime later = LocalDateTime.now().plusHours(1);
    private static BookingDto dto = BookingDto.builder()
            .id(1L)
            .itemId(1L)
            .bookerId(1L)
            .status(BookingStatus.APPROVED)
            .start(now)
            .end(later)
            .build();

    @Test
    void test() throws IOException {
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.booker").isEqualTo(dto.getBookerId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.APPROVED.toString());
    }
}
