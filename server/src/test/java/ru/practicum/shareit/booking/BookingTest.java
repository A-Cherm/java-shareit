package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BookingTest {

    @Test
    void testEquals() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking1 = new Booking(1L, null, null, now, now.plusMonths(1), BookingStatus.APPROVED);
        Booking booking2 = new Booking(1L, null, null, now, now.plusMonths(2), BookingStatus.REJECTED);
        Booking booking3 = new Booking(2L, null, null, now, now.plusMonths(1), BookingStatus.APPROVED);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
    }
}