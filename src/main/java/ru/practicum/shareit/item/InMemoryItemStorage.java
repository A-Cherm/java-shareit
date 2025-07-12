package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    public List<Item> getUserItems(Long userId) {
        return items.values()
                .stream()
                .filter(item -> Objects.equals(item.getUser().getId(), userId))
                .toList();
    }

    public Item getItem(long id) {
        validateId(id);
        return items.get(id);
    }

    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public void deleteItem(long id) {
        items.remove(id);
    }

    public void deleteUserItems(long userId) {
        items.keySet()
                .stream()
                .filter(id -> items.get(id).getUser().getId().equals(userId))
                .forEach(items::remove);
    }

    public List<Item> searchItems(String searchQuery) {
        String lowerCase = searchQuery.toLowerCase();

        return items.values()
                .stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerCase) ||
                        item.getDescription().toLowerCase().contains(searchQuery))
                .toList();
    }

    public void validateId(long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Нет предмета с id = " + id);
        }
    }

    private long getNextId() {
        long maxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
