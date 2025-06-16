package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getUserItems(long userId);

    ItemDto getItem(long userId, long id);

    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemUpdateDto itemDto);

    List<ItemDto> searchItems(long userId, String searchQuery);
}
