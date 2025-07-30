package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CommentTest {

    @Test
    void testEquals() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment1 = new Comment(1L, null, null, "text", now);
        Comment comment2 = new Comment(1L, null, null, "aaa", now.plusDays(1));
        Comment comment3 = new Comment(2L, null, null, "text", now);

        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
    }
}