package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemDto> userItems = itemService.getUserItems(userId);

        log.info("Список вещей пользователя с id = {}: {}", userId, userItems);
        return userItems;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        ItemDto itemDto = itemService.getItem(userId, itemId);

        log.info("Возвращается вещь {}", itemDto);
        return itemDto;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.createItem(userId, itemDto);

        log.info("Создана вещь {}", item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @Valid @RequestBody ItemUpdateDto itemDto) {
        itemDto.setId(itemId);
        ItemDto item = itemService.updateItem(userId, itemDto);

        log.info("Обновлена вещь {}", item);
        return item;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(name = "text") String searchQuery) {
        List<ItemDto> searchResult = itemService.searchItems(userId, searchQuery);

        log.info("Результат поиска по запросу {}: {}", searchQuery, searchResult);
        return searchResult;
    }
}
