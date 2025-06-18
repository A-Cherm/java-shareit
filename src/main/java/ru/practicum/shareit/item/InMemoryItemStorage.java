package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getItemsList(Set<Long> itemsIds) {
        return itemsIds
                .stream()
                .map(items::get)
                .toList();
    }

    @Override
    public Item getItem(long id) {
        validateId(id);
        return items.get(id);
    }

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteItem(long id) {
        items.remove(id);
    }

    @Override
    public List<Item> searchItems(String searchQuery) {
        String lowerCase = searchQuery.toLowerCase();

        return items.values()
                .stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerCase) ||
                        item.getDescription().toLowerCase().contains(searchQuery))
                .toList();
    }

    @Override
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
