package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    List<ItemBookingsDto> getUserItems(long userId);

    ItemBookingsDto getItem(long userId, long id);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemUpdateDto itemDto);

    List<ItemDto> searchItems(long userId, String searchQuery);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

    Item validateItemId(long id);
}
