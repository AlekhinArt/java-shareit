package ru.practicum.shareit.resttests;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithUserAndItem;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ErrorHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)

public class BookingTest {
    @Mock
    BookingService bookingService;
    @InjectMocks
    private BookingController controller;
    private MockMvc mvc;

    private BookingDtoWithUserAndItem bookingDtoWithUserAndItem;
    private static final LocalDateTime start = LocalDateTime.now();
    private static final LocalDateTime end = LocalDateTime.now().plusHours(2);
    private static final String startStr = "2022-12-31T10:00:00";
    private static final String endStr = "2022-12-31T20:00:00";

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();

        bookingDtoWithUserAndItem = bookingDtoWithUserAndItem.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(BookingDto.class), anyLong()))
                .thenReturn(bookingDtoWithUserAndItem);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content("{\"itemId\":2,\"start\":\"" + startStr + "\",\"" + endStr + "\":\"{{end}}\"}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());


    }

    @Test
    void approvalBooking() throws Exception {
        when(bookingService.approvalBooking(anyBoolean(), anyLong(), anyLong()))
                .thenReturn(bookingDtoWithUserAndItem);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDtoWithUserAndItem);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoWithUserAndItem.getId()), Long.class))
                .andDo(print());
    }

    @Test
    void getBookingsUser() throws Exception {
        when(bookingService.getBookingsUser(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoWithUserAndItem));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoWithUserAndItem.getId()), Long.class))
                .andDo(print());
    }

    @Test
    void getBookingsItemOwner() throws Exception {
        when(bookingService.getBookingsItemOwner(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoWithUserAndItem));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoWithUserAndItem.getId()), Long.class))
                .andDo(print());
    }


}
