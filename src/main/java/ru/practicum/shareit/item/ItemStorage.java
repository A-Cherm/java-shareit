package ru.practicum.shareit.item;

import java.util.List;
import java.util.Set;

public interface ItemStorage {
    List<Item> getItemsList(Set<Long> itemsIds);

    Item getItem(long id);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(long id);

    List<Item> searchItems(String searchQuery);

    void validateId(long id);
}
