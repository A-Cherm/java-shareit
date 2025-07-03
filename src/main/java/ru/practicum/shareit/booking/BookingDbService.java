package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookingDbService implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingDbService(BookingRepository bookingRepository,
                            @Qualifier("userDbService") UserService userService,
                            @Qualifier("itemDbService") ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = validateBookingId(bookingId);

        if ((userId != booking.getUser().getId()) &&
                (userId != booking.getItem().getUser().getId())) {
            throw new ValidationException("Данные бронирования доступны только автору или владельцу вещи");
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingCreateDto bookingDto) {
        validateTimeframe(bookingDto.getStart(), bookingDto.getEnd());
        User user = userService.validateUserId(userId);
        Item item = itemService.validateItemId(bookingDto.getItemId());

        if (!item.isAvailable()) {
            throw new ValidationException("Вещь с id = " + item.getId() + " не доступна");
        }
        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingDto, user, item));

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        Booking booking = validateBookingId(bookingId);

        if (booking.getItem().getUser().getId() != userId) {
            throw new ValidationException("Статус может менять только владелец вещи");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Нельзя менять статус рассмотренных заявок");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, BookingState state) {
        userService.validateUserId(userId);
        List<Booking> userBookings;

        userBookings = switch (state) {
            case ALL -> bookingRepository.findAllByUserIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByUserId(userId,
                    LocalDateTime.now());
            case PAST -> bookingRepository.findAllByUserIdAndEndBeforeOrderByStartDesc(userId,
                    LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByUserIdAndStartAfterOrderByStartDesc(userId,
                    LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByUserIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByUserIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.REJECTED);
        };
        return userBookings
                .stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsForUserItems(long userId, BookingState state) {
        userService.validateUserId(userId);
        List<Booking> userBookings;

        userBookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemUserIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findCurrentByOwnerId(userId,
                    LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemUserIdAndEndBeforeOrderByStartDesc(userId,
                    LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemUserIdAndStartAfterOrderByStartDesc(userId,
                    LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId,
                    BookingStatus.REJECTED);
        };
        return userBookings
                .stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public Booking validateBookingId(long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет бронирования с id = " + id));
    }

    private void validateTimeframe(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Некорректный интервал бронирования");
        }
    }
}
