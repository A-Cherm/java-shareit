package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.BookingDbService;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.request.ItemRequestDbService;
import ru.practicum.shareit.user.UserDbService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@Import({ItemDbService.class, UserDbService.class, BookingDbService.class, ItemRequestDbService.class})
class ItemDbServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private static final UserDto userDto1 = new UserDto(null, "user1", "a@mail");
    private static final UserDto userDto2 = new UserDto(null, "user2", "b@mail");
    private static final ItemDto itemDto1 = new ItemDto(null, "item1", "some item", true, null, null);
    private static final ItemDto itemDto2 = new ItemDto(null, "item2", "some item", true, null, null);
    private static final ItemDto itemDto3 = new ItemDto(null, "item3", "some item", false, null, null);

    @Autowired
    public ItemDbServiceTest(@Qualifier("itemDbService") ItemService itemService,
                             @Qualifier("userDbService") UserService userService,
                             BookingService bookingService) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @Test
    void testGetUserItems() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        itemService.createItem(user1.getId(), itemDto2);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto bookingDto1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.plusDays(1));
        BookingCreateDto bookingDto2 = new BookingCreateDto(item1.getId(),
                now.plusMonths(1), now.plusMonths(2));
        BookingDto booking1 = bookingService.createBooking(user2.getId(), bookingDto1);
        BookingDto booking2 = bookingService.createBooking(user2.getId(), bookingDto2);
        bookingService.updateBooking(user1.getId(), booking1.getId(), true);
        bookingService.updateBooking(user1.getId(), booking2.getId(), true);

        List<ItemBookingsDto> userItems = itemService.getUserItems(user1.getId());

        assertNotNull(userItems, "Вещи не возвращается");
        assertEquals(2, userItems.size(), "Неверное число вещей пользователя");
        assertEquals(now.minusMonths(1), userItems.getFirst().getLastBooking(),
                "Неверная дата бронирования");
        assertEquals(now.plusMonths(1), userItems.getFirst().getNextBooking(),
                "Неверная дата бронирования");

        userItems = itemService.getUserItems(user2.getId());

        assertNotNull(userItems, "Вещи не возвращается");
        assertEquals(0, userItems.size(), "Неверное число вещей пользователя");
    }

    @Test
    void testGetItem() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto bookingDto1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.minusDays(1));
        BookingCreateDto bookingDto2 = new BookingCreateDto(item1.getId(),
                now.plusMonths(1), now.plusMonths(2));
        CommentDto comment = new CommentDto(null, "someone", "bad", null);
        BookingDto booking1 = bookingService.createBooking(user2.getId(), bookingDto1);
        bookingService.createBooking(user2.getId(), bookingDto2);
        bookingService.updateBooking(user1.getId(), booking1.getId(), true);
        itemService.addComment(user2.getId(), item1.getId(), comment);

        ItemBookingsDto responseItem = itemService.getItem(user1.getId(), item1.getId());

        assertNotNull(responseItem, "Вещь не возвращается");
        assertEquals("item1", responseItem.getName(), "Неверное название вещи");
        assertNull(responseItem.getLastBooking(), "Неверная дата бронирования");
        assertEquals(now.plusMonths(1), responseItem.getNextBooking(),
                "Неверная дата бронирования");
        assertNotNull(responseItem.getComments(), "Комментарии не возвращаются");
        assertEquals(1, responseItem.getComments().size(), "Неверное число комментариев");
        assertEquals("user2", responseItem.getComments().getFirst().getAuthorName(),
                "Неверный автор комментария");
        assertEquals("bad", responseItem.getComments().getFirst().getText(),
                "Неверный текст комментария");
    }

    @Test
    void testCreateItem() {
        UserDto user1 = userService.createUser(userDto1);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);

        assertNotNull(item1, "Вещь не возвращается");
        assertEquals("item1", item1.getName(), "Неверное название вещи");
        assertEquals("some item", item1.getDescription(), "Неверное описание вещи");
        assertTrue(item1.getAvailable(), "Неверный статус вещи");
    }

    @Test
    void testUpdateItem() {
        UserDto user1 = userService.createUser(userDto1);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        ItemUpdateDto newItemDto = new ItemUpdateDto(item1.getId(), "new name", "aaa", false);
        ItemDto newItem = itemService.updateItem(user1.getId(), newItemDto);

        assertNotNull(newItem, "Вещь не возвращается");
        assertEquals("new name", newItem.getName(), "Неверное название вещи");
        assertEquals("aaa", newItem.getDescription(), "Неверное описание вещи");
        assertFalse(newItem.getAvailable(), "Неверный статус вещи");
    }

    @Test
    void testSearchItems() {
        UserDto user1 = userService.createUser(userDto1);
        itemService.createItem(user1.getId(), itemDto1);
        itemService.createItem(user1.getId(), itemDto2);
        itemService.createItem(user1.getId(), itemDto3);

        List<ItemDto> search = itemService.searchItems(user1.getId(), "it");

        assertNotNull(search, "Список вещей не возвращается");
        assertEquals(2, search.size(), "Неверный размер списка");

        search = itemService.searchItems(user1.getId(), "om");

        assertNotNull(search, "Список вещей не возвращается");
        assertEquals(2, search.size(), "Неверный размер списка");

        search = itemService.searchItems(user1.getId(), "em2");

        assertNotNull(search, "Список вещей не возвращается");
        assertEquals(1, search.size(), "Неверный размер списка");
        assertEquals("item2", search.getFirst().getName(), "Неверное название вещи");

        search = itemService.searchItems(user1.getId(), "asd");

        assertNotNull(search, "Список вещей не возвращается");
        assertEquals(0, search.size(), "Неверный размер списка");
    }

    @Test
    void testAddComment() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);
        ItemDto item1 = itemService.createItem(user1.getId(), itemDto1);
        ItemDto item2 = itemService.createItem(user1.getId(), itemDto2);
        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto bookingDto1 = new BookingCreateDto(item1.getId(),
                now.minusMonths(1), now.minusDays(1));
        CommentDto comment = new CommentDto(null, "someone", "bad", null);
        BookingDto booking1 = bookingService.createBooking(user2.getId(), bookingDto1);
        bookingService.updateBooking(user1.getId(), booking1.getId(), true);
        CommentDto newComment = itemService.addComment(user2.getId(), item1.getId(), comment);

        assertNotNull(newComment, "Комментарий не возвращается");
        assertEquals("user2", newComment.getAuthorName(), "Неверный автор комментария");
        assertEquals("bad", newComment.getText(), "Неверный текст комментария");
        assertNotNull(newComment.getCreated(), "Дата комментария не возвращается");

        assertThrows(ValidationException.class,
                () -> itemService.addComment(user2.getId(), item2.getId(), comment));
    }
}