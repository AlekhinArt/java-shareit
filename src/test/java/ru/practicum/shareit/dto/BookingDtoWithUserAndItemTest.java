package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoWithUserAndItemTest {
    @Autowired
    private JacksonTester<BookingDtoWithUserAndItem> json;
    private static final LocalDateTime now = LocalDateTime.now();
    private static final LocalDateTime later = LocalDateTime.now().plusHours(1);
    private static User user = User.builder()
            .id(1L)
            .name("Test")
            .email("Test@Test.ru")
            .build();
    private Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("item description")
            .ownerId(1L)
            .available(true)
            .requestId(0L)
            .build();

    private BookingDtoWithUserAndItem dto = BookingDtoWithUserAndItem.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .start(now)
            .end(later)
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void test() throws IOException {
        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.WAITING.toString());

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(dto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(dto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(dto.getBooker().getEmail());

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(dto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(dto.getItem().getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(dto.getItem().getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.item.owner").isEqualTo(dto.getItem().getOwnerId().intValue());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(dto.getItem().getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(dto.getItem().getRequestId().intValue());
    }

}
