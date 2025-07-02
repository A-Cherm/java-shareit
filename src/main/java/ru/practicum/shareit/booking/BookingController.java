package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId) {
        BookingDto booking = bookingService.getBooking(bookingId);

        log.info("Возвращается бронирование {}", booking);
        return booking;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody BookingCreateDto bookingDto) {
        BookingDto booking = bookingService.createBooking(userId, bookingDto);

        log.info("Создано бронирование {}", booking);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long bookingId,
                                    @RequestParam boolean approved) {
        BookingDto booking = bookingService.updateBooking(userId, bookingId, approved);

        log.info("Обновлено бронирование {}", booking);
        return booking;
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        List<BookingDto> bookings = bookingService.getUserBookings(userId, state);

        log.info("Возвращаются бронирования пользователя {} со статусом {}: {}", userId, state, bookings);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "ALL") BookingState state) {
        List<BookingDto> bookings = bookingService.getBookingsForUserItems(userId, state);

        log.info("Возвращаются бронирования вещей владельца {} со статусом {}: {}", userId, state, bookings);
        return bookings;
    }
}
