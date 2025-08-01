package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDbService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestDbService;
import ru.practicum.shareit.user.UserDbService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BookingDbService.class, UserDbService.class, ItemDbService.class, ItemRequestDbService.class})
class BookingDbServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private static final UserDto userDto1 = new UserDto(null, "user1", "a@mail");
    private static final UserDto userDto2 = new UserDto(null, "user2", "b@mail");
    private static final ItemDto itemDto1 = new ItemDto(null, "item1", "some item", true, null, null);
    private static final ItemDto itemDto2 = new ItemDto(null, "item2", "some item", true, null, null);

    @Autowired
    public BookingDbServiceTest(BookingService bookingService,
                                @Qualifier("userDbService") UserService userService,
                                @Qualifier("itemDbService") ItemService itemService) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Test
    void testGetBooking() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));

        BookingDto createdBooking = bookingService.createBooking(user2.getId(), booking1);
        BookingDto returnedBooking = bookingService.getBooking(user2.getId(), createdBooking.getId());

        assertNotNull(returnedBooking, "Бронирование не возвращается");
        assertEquals(now.minusMonths(1), returnedBooking.getStart(), "Неверная дата бронирования");
        assertEquals(now.plusDays(1), returnedBooking.getEnd(), "Неверная дата бронирования");
        assertEquals(user2.getName(), returnedBooking.getBooker().getName(), "Неверное имя пользователя");

        returnedBooking = bookingService.getBooking(user1.getId(), createdBooking.getId());

        assertNotNull(returnedBooking, "Бронирование не возвращается");
        assertEquals(now.minusMonths(1), returnedBooking.getStart(), "Неверная дата бронирования");
        assertEquals(now.plusDays(1), returnedBooking.getEnd(), "Неверная дата бронирования");
        assertEquals(user2.getName(), returnedBooking.getBooker().getName(), "Неверное имя пользователя");
    }

    @Test
    void testGetBookingByInvalidUser() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        UserDto user3 = userService.createUser(new UserDto(null, "user3", "d@mail"));
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));

        BookingDto createdBooking = bookingService.createBooking(user2.getId(), booking1);

        assertThrows(ValidationException.class,
                () -> bookingService.getBooking(user3.getId(), createdBooking.getId()));
    }


    @Test
    void testCreateBooking() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));

        BookingDto createdBooking = bookingService.createBooking(user2.getId(), booking1);

        assertNotNull(createdBooking, "Бронирование не возвращается");
        assertEquals(now.minusMonths(1), createdBooking.getStart(), "Неверная дата бронирования");
        assertEquals(now.plusDays(1), createdBooking.getEnd(), "Неверная дата бронирования");
        assertEquals(user2.getName(), createdBooking.getBooker().getName(), "Неверное имя пользователя");
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus(), "Неверный статус бронирования");
    }

    @Test
    void testCreateBookingForUnavailableItem() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(),
                new ItemDto(null, "item", "asd", false, null, null));
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(user2.getId(), booking1));
    }

    @Test
    void testUpdateBooking() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));

        BookingDto createdBooking = bookingService.createBooking(user2.getId(), booking1);
        BookingDto updatedBooking = bookingService.updateBooking(user1.getId(),
                createdBooking.getId(), true);

        assertNotNull(updatedBooking, "Бронирование не возвращается");
        assertEquals(now.minusMonths(1), updatedBooking.getStart(), "Неверная дата бронирования");
        assertEquals(now.plusDays(1), updatedBooking.getEnd(), "Неверная дата бронирования");
        assertEquals(user2.getName(), updatedBooking.getBooker().getName(), "Неверное имя пользователя");
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus(), "Неверный статус бронирования");
    }

    @Test
    void testUpdateBookingByInvalidUser() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));
        BookingDto createdBooking = bookingService.createBooking(user2.getId(), booking1);

        assertThrows(ValidationException.class,
                () -> bookingService.updateBooking(user2.getId(), createdBooking.getId(), true));
    }

    @Test
    void testUpdateApprovedBooking() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));
        BookingDto createdBooking = bookingService.createBooking(user2.getId(), booking1);
        bookingService.updateBooking(user1.getId(), createdBooking.getId(), true);

        assertThrows(ValidationException.class,
                () -> bookingService.updateBooking(user1.getId(), createdBooking.getId(), false));
    }

    @Test
    void testGetUserBookings() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));
        BookingCreateDto booking2 = new BookingCreateDto(item1.getId(),
                now.minusMonths(2), now.minusMonths(1));
        BookingCreateDto booking3 = new BookingCreateDto(item1.getId(),
                now.plusMonths(1), now.plusMonths(2));

        BookingDto createdBooking1 = bookingService.createBooking(user2.getId(), booking1);
        BookingDto createdBooking2 = bookingService.createBooking(user2.getId(), booking2);
        BookingDto createdBooking3 = bookingService.createBooking(user2.getId(), booking3);

        List<BookingDto> userBookings = bookingService.getUserBookings(user2.getId(), BookingState.WAITING);

        assertNotNull(userBookings, "Список бронирований не возвращается");
        assertEquals(3, userBookings.size(), "Неверное число бронирований");

        bookingService.updateBooking(user1.getId(), createdBooking1.getId(), false);
        bookingService.updateBooking(user1.getId(), createdBooking2.getId(), true);

        userBookings = bookingService.getUserBookings(user2.getId(), BookingState.ALL);

        assertNotNull(userBookings, "Список бронирований не возвращается");
        assertEquals(3, userBookings.size(), "Неверное число бронирований");

        userBookings = bookingService.getUserBookings(user2.getId(), BookingState.CURRENT);

        assertNotNull(userBookings, "Список бронирований не возвращается");
        assertEquals(1, userBookings.size(), "Неверное число бронирований");
        assertEquals(createdBooking1.getId(), userBookings.getFirst().getId(), "Неверное бронирование");

        userBookings = bookingService.getUserBookings(user2.getId(), BookingState.PAST);

        assertNotNull(userBookings, "Список бронирований не возвращается");
        assertEquals(1, userBookings.size(), "Неверное число бронирований");
        assertEquals(createdBooking2.getId(), userBookings.getFirst().getId(), "Неверное бронирование");

        userBookings = bookingService.getUserBookings(user2.getId(), BookingState.FUTURE);

        assertNotNull(userBookings, "Список бронирований не возвращается");
        assertEquals(1, userBookings.size(), "Неверное число бронирований");
        assertEquals(createdBooking3.getId(), userBookings.getFirst().getId(), "Неверное бронирование");

        userBookings = bookingService.getUserBookings(user2.getId(), BookingState.REJECTED);

        assertNotNull(userBookings, "Список бронирований не возвращается");
        assertEquals(1, userBookings.size(), "Неверное число бронирований");
        assertEquals(createdBooking1.getId(), userBookings.getFirst().getId(), "Неверное бронирование");

        userBookings = bookingService.getUserBookings(user1.getId(), BookingState.ALL);

        assertNotNull(userBookings, "Список бронирований не возвращается");
        assertEquals(0, userBookings.size(), "Неверное число бронирований");
    }

    @Test
    void testGetBookingsForUserItems() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        ItemDto item2 = itemService.createItem(user2.getId(), itemDto2);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));
        BookingCreateDto booking2 = new BookingCreateDto(item1.getId(),
                now.minusMonths(2), now.minusMonths(1));
        BookingCreateDto booking3 = new BookingCreateDto(item1.getId(),
                now.plusMonths(1), now.plusMonths(2));
        BookingCreateDto booking4 = new BookingCreateDto(item2.getId(),
                now.plusMonths(1), now.plusMonths(2));

        BookingDto createdBooking1 = bookingService.createBooking(user2.getId(), booking1);
        BookingDto createdBooking2 = bookingService.createBooking(user2.getId(), booking2);
        BookingDto createdBooking3 = bookingService.createBooking(user2.getId(), booking3);
        BookingDto createdBooking4 = bookingService.createBooking(user1.getId(), booking4);

        List<BookingDto> ownerBookings = bookingService.getBookingsForUserItems(user1.getId(), BookingState.WAITING);

        assertNotNull(ownerBookings, "Список бронирований не возвращается");
        assertEquals(3, ownerBookings.size(), "Неверное число бронирований");

        bookingService.updateBooking(user1.getId(), createdBooking1.getId(), false);
        bookingService.updateBooking(user1.getId(), createdBooking2.getId(), true);

        ownerBookings = bookingService.getBookingsForUserItems(user1.getId(), BookingState.ALL);

        assertNotNull(ownerBookings, "Список бронирований не возвращается");
        assertEquals(3, ownerBookings.size(), "Неверное число бронирований");

        ownerBookings = bookingService.getBookingsForUserItems(user2.getId(), BookingState.ALL);

        assertNotNull(ownerBookings, "Список бронирований не возвращается");
        assertEquals(1, ownerBookings.size(), "Неверное число бронирований");
        assertEquals(createdBooking4.getId(), ownerBookings.getFirst().getId(), "Неверное бронирование");

        ownerBookings = bookingService.getBookingsForUserItems(user1.getId(), BookingState.PAST);

        assertNotNull(ownerBookings, "Список бронирований не возвращается");
        assertEquals(1, ownerBookings.size(), "Неверное число бронирований");
        assertEquals(createdBooking2.getId(), ownerBookings.getFirst().getId(), "Неверное бронирование");

        ownerBookings = bookingService.getBookingsForUserItems(user1.getId(), BookingState.FUTURE);

        assertNotNull(ownerBookings, "Список бронирований не возвращается");
        assertEquals(1, ownerBookings.size(), "Неверное число бронирований");
        assertEquals(createdBooking3.getId(), ownerBookings.getFirst().getId(), "Неверное бронирование");

        ownerBookings = bookingService.getBookingsForUserItems(user1.getId(), BookingState.REJECTED);

        assertNotNull(ownerBookings, "Список бронирований не возвращается");
        assertEquals(1, ownerBookings.size(), "Неверное число бронирований");
        assertEquals(createdBooking1.getId(), ownerBookings.getFirst().getId(), "Неверное бронирование");
    }

    @Test
    void testValidateBookingId() {
        UserDto user = userService.createUser(userDto1);
        ItemDto item = itemService.createItem(user.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking1 = new BookingCreateDto(item.getId(),
                now.minusMonths(1), now.plusDays(1));
        BookingDto booking = bookingService.createBooking(user.getId(), booking1);

        Booking validatedBooking = bookingService.validateBookingId(booking.getId());

        assertNotNull(validatedBooking, "Бронирование не возвращается");
        assertEquals(now.minusMonths(1), validatedBooking.getStart(), "Неверная дата бронирования");
        assertEquals(now.plusDays(1), validatedBooking.getEnd(), "Неверная дата бронирования");
        assertEquals(BookingStatus.WAITING, validatedBooking.getStatus(), "Неверный статус бронирования");

        assertThrows(NotFoundException.class,
                () -> bookingService.validateBookingId(booking.getId() + 1));
    }
}