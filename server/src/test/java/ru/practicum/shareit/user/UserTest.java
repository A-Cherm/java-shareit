package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    @Test
    void testEquals() {
        User user1 = new User(1L, "user1", "aaa");
        User user2 = new User(1L, "user2", "bbb");
        User user3 = new User(2L, "user1", "aaa");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }
}