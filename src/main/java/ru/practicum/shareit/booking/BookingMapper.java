package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                UserMapper.mapToUserDto(booking.getUser()),
                ItemMapper.mapToItemDto(booking.getItem(), List.of()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static Booking mapToBooking(BookingCreateDto bookingDto, User user, Item item) {
        validateTimeframe(bookingDto.getStart(), bookingDto.getEnd());

        return new Booking(
                null,
                user,
                item,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                BookingStatus.WAITING
        );
    }

    private static void validateTimeframe(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Некорректный интервал бронирования");
        }
    }
}
