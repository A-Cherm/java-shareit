package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("itemDbService")
@Slf4j
@Transactional(readOnly = true)
public class ItemDbService implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemDbService(ItemRepository itemRepository, BookingRepository bookingRepository,
                         CommentRepository commentRepository,
                         @Qualifier("userDbService") UserService userService,
                         ItemRequestService itemRequestService) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.itemRequestService = itemRequestService;
    }

    @Override
    public List<ItemBookingsDto> getUserItems(long userId) {
        userService.validateUserId(userId);
        List<Booking> itemsBookings = bookingRepository
                .findAllByItemUserIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED);
        List<Item> userItems = itemRepository.findAllByUserId(userId);
        LocalDateTime now = LocalDateTime.now();
        Map<Long, LocalDateTime> lastBookingDates = new HashMap<>();
        Map<Long, LocalDateTime> nextBookingDates = new HashMap<>();

        for (Booking booking : itemsBookings) {
            LocalDateTime last = lastBookingDates.get(booking.getItem().getId());
            LocalDateTime next = nextBookingDates.get(booking.getItem().getId());

            if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now) &&
                    (last == null || (last.isBefore(booking.getStart())))) {
                lastBookingDates.put(booking.getItem().getId(), booking.getStart());
            } else if (booking.getStart().isAfter(now) &&
                    (next == null || next.isAfter(booking.getStart()))) {
                nextBookingDates.put(booking.getItem().getId(), booking.getStart());
            }
        }
        List<Comment> comments = commentRepository.findAllByItemUserId(userId);
        Map<Long, List<Comment>> itemsComments = new HashMap<>();
        userItems.forEach(item -> itemsComments.put(item.getId(), new ArrayList<>()));

        for (Comment comment : comments) {
            itemsComments.get(comment.getItem().getId()).add(comment);
        }
        return userItems
                .stream()
                .map(item -> ItemMapper.mapToItemBookingsDto(item,
                        itemsComments.get(item.getId()),
                        lastBookingDates.get(item.getId()),
                        nextBookingDates.get(item.getId())))
                .toList();
    }

    @Override
    public ItemBookingsDto getItem(long userId, long id) {
        userService.validateUserId(userId);
        Item item = validateItemId(id);
        List<Comment> comments = commentRepository.findAllByItemId(id);
        List<Booking> bookings = bookingRepository.findAllByItemId(id);
        LocalDateTime last = null;
        LocalDateTime next = null;
        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookings) {
            if (booking.getStart().isBefore(now) && booking.getEnd().isAfter(now) &&
                    (last == null || last.isBefore(booking.getStart()))) {
                last = booking.getStart();
            } else if (booking.getStart().isAfter(now) &&
                    (next == null || next.isAfter(booking.getStart()))) {
                next = booking.getStart();
            }
        }
        return ItemMapper.mapToItemBookingsDto(item, comments, last, next);
    }

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userService.validateUserId(userId);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestService.validateRequestId(itemDto.getRequestId());
        }
        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, user, request));

        return ItemMapper.mapToItemDto(item, List.of());
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, ItemUpdateDto itemDto) {
        userService.validateUserId(userId);
        Item oldItem = validateItemId(itemDto.getId());

        log.debug("Исходные данные вещи: {}", oldItem);
        if (oldItem.getUser().getId() != userId) {
            throw new ValidationException("Неверный id владельца вещи");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
        log.debug("Обновлённые данные вещи: {}", oldItem);

        oldItem = itemRepository.save(oldItem);

        return ItemMapper.mapToItemDto(oldItem, List.of());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String searchQuery) {
        return itemRepository.searchAvailableItems(searchQuery)
                .stream()
                .map(item -> ItemMapper.mapToItemDto(item, List.of()))
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userService.validateUserId(userId);
        Item item = validateItemId(itemId);

        if (!bookingRepository.existsByUserIdAndItemIdAndStatusAndEndBefore(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new ValidationException("Нет подходящих бронирований");
        }
        Comment comment = ItemMapper.mapToComment(commentDto, user, item);

        commentRepository.save(comment);
        return ItemMapper.mapToCommentDto(comment);
    }

    @Override
    public Item validateItemId(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет предмета с id = " + id));
    }
}
