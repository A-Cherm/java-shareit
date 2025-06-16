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
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemStorage itemStorage;
    @Autowired
    private final UserStorage userStorage;

    @Override
    public List<ItemDto> getUserItems(long userId) {
        User user = userStorage.getUser(userId);
        List<Item> userItems = itemStorage.getItemsList(user.getItemIds());

        log.info("Список вещей пользователя: {}", userItems);
        return userItems
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto getItem(long userId, long id) {
        Item item = itemStorage.getItem(id);

        log.info("Возвращается вещь {}", item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userStorage.validateId(userId);

        Item item = itemStorage.createItem(ItemMapper.mapToItem(itemDto, userId));

        userStorage.addItem(item.getUserId(), item.getId());
        log.info("Создана вещь {}", item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, ItemUpdateDto itemDto) {
        itemStorage.validateId(itemDto.getId());
        userStorage.validateId(userId);

        Item oldItem = itemStorage.getItem(itemDto.getId());

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

        oldItem = itemStorage.updateItem(oldItem);

        log.info("Обновлена вещь {}", oldItem);
        return ItemMapper.mapToItemDto(oldItem);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String searchQuery) {
        List<Item> searchResult = itemStorage.searchItems(searchQuery);

        log.info("Результат поиска по запросу {}: {}", searchQuery, searchResult);
        return searchResult
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
