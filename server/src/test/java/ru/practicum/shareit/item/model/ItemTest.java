package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemTest {

    @Test
    void testEquals() {
        Item item1 = new Item(1L, null, "item1", "aaa", true, null);
        Item item2 = new Item(1L, null, "item2", "bbb", false, null);
        Item item3 = new Item(2L, null, "item1", "aaa", true, null);

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
    }
}