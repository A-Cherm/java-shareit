package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;

import java.util.List;

@Service("ItemServiceImpl")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage itemStorage;
    private final InMemoryUserStorage userStorage;
    private final ItemRequestService itemRequestService;

    @Override
    public List<ItemBookingsDto> getUserItems(long userId) {
        List<Item> userItems = itemStorage.getUserItems(userId);

        return userItems
                .stream()
                .map(item -> ItemMapper.mapToItemBookingsDto(item, List.of(), null, null))
                .toList();
    }

    @Override
    public ItemBookingsDto getItem(long userId, long id) {
        Item item = itemStorage.getItem(id);

        return ItemMapper.mapToItemBookingsDto(item, List.of(), null, null);
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userStorage.getUser(userId);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestService.validateRequestId(itemDto.getRequestId());
        }
        Item item = itemStorage.createItem(ItemMapper.mapToItem(itemDto, user, request));

        return ItemMapper.mapToItemDto(item, List.of());
    }

    @Override
    public ItemDto updateItem(long userId, ItemUpdateDto itemDto) {
        userStorage.validateId(userId);

        Item oldItem = itemStorage.getItem(itemDto.getId());

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

        oldItem = itemStorage.updateItem(oldItem);

        return ItemMapper.mapToItemDto(oldItem, List.of());
    }

    @Override
    public List<ItemDto> searchItems(long userId, String searchQuery) {
        List<Item> searchResult = itemStorage.searchItems(searchQuery);

        return searchResult
                .stream()
                .map(item -> ItemMapper.mapToItemDto(item, List.of()))
                .toList();
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        return null;
    }

    @Override
    public Item validateItemId(long id) {
        return itemStorage.getItem(id);
    }
}
