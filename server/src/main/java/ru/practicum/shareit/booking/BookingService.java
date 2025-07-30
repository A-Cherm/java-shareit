package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto getBooking(long userId, long bookingId);

    BookingDto createBooking(long userId, BookingCreateDto bookingDto);

    BookingDto updateBooking(long userId, long bookingId, boolean approved);

    List<BookingDto> getUserBookings(long userId, BookingState state);

    List<BookingDto> getBookingsForUserItems(long userId, BookingState state);

    Booking validateBookingId(long id);
}
