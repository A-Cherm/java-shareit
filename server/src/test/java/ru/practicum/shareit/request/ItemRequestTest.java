package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemRequestTest {

    @Test
    void testEquals() {
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest1 = new ItemRequest(1L, null, "aaa", now);
        ItemRequest itemRequest2 = new ItemRequest(1L, null, "bbb", now.plusDays(1));
        ItemRequest itemRequest3 = new ItemRequest(2L, null, "aaa", now);

        assertEquals(itemRequest1, itemRequest2);
        assertNotEquals(itemRequest1, itemRequest3);
    }
}