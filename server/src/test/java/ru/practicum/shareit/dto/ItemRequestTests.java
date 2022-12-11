package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestTests {

    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private static final LocalDateTime now = LocalDateTime.now();
    Collection<ItemDto> itemDto = List.of(ItemDto.builder()
            .id(1L)
            .name("testName")
            .description("Test description")
            .ownerId(1L)
            .available(true)
            .requestId(0L)
            .build());
    private ItemRequestDto dto = new ItemRequestDto(1L, "request",
            1L,
            now, itemDto);


    @Test
    void test() throws IOException {
        List<ItemDto> itemDtoList = new ArrayList<>(itemDto);


        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requestor");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(itemDtoList.get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo(itemDtoList.get(0).getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo(itemDtoList.get(0).getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].owner").isEqualTo(itemDtoList.get(0).getOwnerId().intValue());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo((itemDtoList.get(0).getAvailable()));
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(itemDtoList.get(0).getRequestId().intValue());
    }

}
