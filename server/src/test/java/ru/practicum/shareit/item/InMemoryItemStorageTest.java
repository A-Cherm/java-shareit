package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryItemStorageTest {
    private InMemoryItemStorage itemStorage;
    private static final User user1 = new User(1L, "user1", "a@mail");
    private static final User user2 = new User(2L, "user2", "b@mail");
    private static final Item item1 = new Item(1L, user1, "item1", "some item", true, null);
    private static final Item item2 = new Item(1L, user2, "item2", "some item", true, null);
    private static final Item item3 = new Item(1L, user1, "item3", "some item", false, null);

    @BeforeEach
    public void newStorage() {
        itemStorage = new InMemoryItemStorage();
    }

    @Test
    void testGetItemsList() {
        itemStorage.createItem(item1);
        itemStorage.createItem(item2);
        itemStorage.createItem(item3);

        List<Item> items = itemStorage.getUserItems(1L);

        assertNotNull(items, "Список вещей не инициализирован");
        assertEquals(2, items.size(), "Неверный размер списка");
    }

    @Test
    void testGetItem() {
        itemStorage.createItem(item1);

        Item item = itemStorage.getItem(1L);

        assertNotNull(item, "Вещь не возвращается");
        assertEquals(1L, item.getId(), "Неверный id вещи");
        assertEquals("item1", item.getName(), "Неверное название вещи");
        assertTrue(item.isAvailable(), "Неверный статус вещи");
    }

    @Test
    void testCreateItem() {
        Item item = itemStorage.createItem(item1);

        assertNotNull(item, "Вещь не возвращается");
        assertEquals(1L, item.getId(), "Неверный id вещи");
        assertEquals("item1", item.getName(), "Неверное название вещи");
        assertTrue(item.isAvailable(), "Неверный статус вещи");
    }

    @Test
    void testUpdateItem() {
        itemStorage.createItem(item1);

        Item newItem = new Item(1L, user1, "new item", "new", false, null);

        itemStorage.updateItem(newItem);

        assertNotNull(newItem, "Вещь не возвращается");
        assertEquals(1L, newItem.getId(), "Неверный id вещи");
        assertEquals("new item", newItem.getName(), "Неверное название вещи");
        assertEquals("new", newItem.getDescription(), "Неверное описание вещи");
        assertFalse(newItem.isAvailable(), "Неверный статус вещи");
    }

    @Test
    void testSearchItems() {
        itemStorage.createItem(item1);
        itemStorage.createItem(item2);
        itemStorage.createItem(item3);

        List<Item> search = itemStorage.searchItems("om");

        assertNotNull(search, "Список вещей не инициализирован");
        assertEquals(2, search.size(), "Неверный размер списка");
        assertEquals(1L, search.getFirst().getId(), "Неверный id вещи");
    }

    @Test
    void testDeleteUserItems() {
        itemStorage.createItem(item1);
        itemStorage.createItem(item2);
        itemStorage.createItem(item3);

        itemStorage.deleteUserItems(user1.getId());

        List<Item> items = itemStorage.getUserItems(user1.getId());

        assertNotNull(items, "Список вещей не инициализирован");
        assertEquals(0, items.size(), "Неверный размер списка");
    }
}