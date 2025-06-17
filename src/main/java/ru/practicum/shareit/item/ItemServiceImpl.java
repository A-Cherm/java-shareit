package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public List<ItemDto> getUserItems(long userId) {
        User user = userStorage.getUser(userId);
        List<Item> userItems = itemStorage.getItemsList(user.getItemIds());

        return userItems
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto getItem(long userId, long id) {
        Item item = itemStorage.getItem(id);

        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userStorage.validateId(userId);

        Item item = itemStorage.createItem(ItemMapper.mapToItem(itemDto, userId));

        userStorage.addItem(item.getUserId(), item.getId());
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, ItemUpdateDto itemDto) {
        userStorage.validateId(userId);

        Item oldItem = itemStorage.getItem(itemDto.getId());

        log.debug("Исходные данные вещи: {}", oldItem);
        if (oldItem.getUserId() != userId) {
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

        return ItemMapper.mapToItemDto(oldItem);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String searchQuery) {
        if (searchQuery == null || searchQuery.isBlank()) {
            return List.of();
        }
        List<Item> searchResult = itemStorage.searchItems(searchQuery);

        return searchResult
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
