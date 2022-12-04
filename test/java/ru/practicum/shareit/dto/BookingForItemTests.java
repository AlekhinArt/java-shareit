package ru.practicum.shareit.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingForItemTests {

    @Autowired
    private JacksonTester<BookingDtoForItem> json;
    private BookingDtoForItem dto = new BookingDtoForItem(2L, 1L);

    @Test
    void test() throws IOException {
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(dto.getBookerId().intValue());
    }
}
