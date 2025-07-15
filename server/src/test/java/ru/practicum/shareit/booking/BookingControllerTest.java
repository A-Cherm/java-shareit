package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;

    private final LocalDateTime startTime = LocalDateTime.now();
    private final LocalDateTime endTime = startTime.plusMonths(1);
    private final UserDto userDto = new UserDto(1L, "name", "a@b");
    private final ItemDto itemDto = new ItemDto(1L, "item", "qwe", true, null, null);
    private final BookingDto bookingDto1 = new BookingDto(1L, userDto, itemDto, startTime, endTime, BookingStatus.APPROVED);
    private final BookingCreateDto bookingCreateDto1 = new BookingCreateDto(1L, startTime, endTime);

    @Autowired
    public BookingControllerTest(MockMvc mvc, ObjectMapper mapper) {
        this.mvc = mvc;
        this.mapper = mapper;
    }

    @Test
    void testGetBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto1);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.start", is(formatter.format(bookingDto1.getStart()))));
    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(bookingDto1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingCreateDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.start", is(formatter.format(bookingDto1.getStart()))));
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto1);

        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.start", is(formatter.format(bookingDto1.getStart()))));
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getUserBookings(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(bookingDto1));

        mvc.perform(get("/bookings?state=FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].start", is(formatter.format(bookingDto1.getStart()))));
    }

    @Test
    void getBookingsForUserItems() throws Exception {
        when(bookingService.getBookingsForUserItems(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(bookingDto1));

        mvc.perform(get("/bookings/owner?state=FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].item.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].start", is(formatter.format(bookingDto1.getStart()))));
    }
}