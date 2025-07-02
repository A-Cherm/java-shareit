package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookingDbService implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingDbService(BookingRepository bookingRepository,
                            UserRepository userRepository,
                            ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto getBooking(long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Нет бронирования с id = " + bookingId));

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingCreateDto bookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id = " + userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Нет вещи с id = " + bookingDto.getItemId()));

        if (!item.isAvailable()) {
            throw new ValidationException("Вещь с id = " + item.getId() + " не доступна");
        }
        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingDto, user, item));

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Нет бронирования с id = " + bookingId));

        if (booking.getItem().getUser().getId() != userId) {
            throw new ValidationException("Статус может менять только владелец вещи");
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
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Нет пользователя с id = " + userId);
        }
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
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Нет пользователя с id = " + userId);
        }
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
}
