package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;

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

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    @Qualifier("itemDbService")
    private ItemService itemService;

    private final ItemDto itemDto1 = new ItemDto(1L, "item1", "some item",
            true, null, null);
    private final ItemDto itemDto2 = new ItemDto(2L, "item2", "some item",
            false, null, null);
    private final ItemBookingsDto itemBookingsDto1 = new ItemBookingsDto(1L, "item1", "some item", true, null,
            LocalDateTime.of(2000, 1, 1, 0, 0, 0),
            LocalDateTime.of(2030, 1, 1, 0, 0, 0));
    private final ItemBookingsDto itemBookingsDto2 = new ItemBookingsDto(2L, "item2", "some item", false, null,
            LocalDateTime.of(2000, 1, 1, 0, 0, 0),
            LocalDateTime.of(2030, 1, 1, 0, 0, 0));
    private final CommentDto commentDto = new CommentDto(1L, "some name", "lol",
            LocalDateTime.of(2000, 1, 1, 0, 0, 0));

    @Test
    void testGetUserItems() throws Exception {
        when(itemService.getUserItems(anyLong()))
                .thenReturn(List.of(itemBookingsDto1, itemBookingsDto2));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemBookingsDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(itemBookingsDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking",
                        is(formatter.format(itemBookingsDto1.getLastBooking()))))
                .andExpect(jsonPath("$.[0].nextBooking",
                        is(formatter.format(itemBookingsDto1.getNextBooking()))));
    }

    @Test
    void testGetItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemBookingsDto1);

        mvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemBookingsDto1.getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking",
                        is(formatter.format(itemBookingsDto1.getLastBooking()))))
                .andExpect(jsonPath("$.nextBooking",
                        is(formatter.format(itemBookingsDto1.getNextBooking()))));
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(itemDto1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any()))
                .thenReturn(itemDto1);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())));
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemService.searchItems(anyLong(), anyString()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mvc.perform(get("/items/search?text=asd")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.[1].description", is(itemDto2.getDescription())));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.created", is(formatter.format(commentDto.getCreated()))));
    }
}