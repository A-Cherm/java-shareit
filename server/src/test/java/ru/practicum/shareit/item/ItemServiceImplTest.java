package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {
    private ItemService itemService;

    private final InMemoryItemStorage itemStorage = Mockito.mock(InMemoryItemStorage.class);
    private final InMemoryUserStorage userStorage = Mockito.mock(InMemoryUserStorage.class);

    private final User user1 = new User(1L, "user1", "a@mail");
    private final Item item1 = new Item(1L, user1, "item1", "some item", true, null);
    private final Item item2 = new Item(1L, user1, "item2", "some item", false, null);
    private final ItemDto itemDto1 = new ItemDto(1L, "item1", "some item", true, null, null);
    private final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(2L, "item2", "some item", true);

    @BeforeEach
    void newService() {
        this.itemService = new ItemServiceImpl(itemStorage, userStorage);
    }

    @Test
    void testGetUserItems() {
        when(itemStorage.getUserItems(anyLong()))
                .thenReturn(List.of(item1, item2));

        List<ItemBookingsDto> userItems = itemService.getUserItems(1L);

        assertThat(userItems, notNullValue());
        assertThat(userItems.size(), equalTo(2));
    }

    @Test
    void testGetItem() {
        when(itemStorage.getItem(anyLong()))
                .thenReturn(item1);

        ItemBookingsDto itemDto = itemService.getItem(1L, 1L);

        assertThat(itemDto, notNullValue());
        assertThat(itemDto.getName(), equalTo(item1.getName()));
        assertThat(itemDto.getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item1.isAvailable()));
    }

    @Test
    void testCreateItem() {
        when(userStorage.getUser(anyLong()))
                .thenReturn(user1);
        when(itemStorage.createItem(any()))
                .thenReturn(item1);

        ItemDto itemDto = itemService.createItem(1L, itemDto1);

        assertThat(itemDto, notNullValue());
        assertThat(itemDto.getName(), equalTo(item1.getName()));
        assertThat(itemDto.getDescription(), equalTo(item1.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item1.isAvailable()));
    }

    @Test
    void testUpdateItem() {
        when(itemStorage.getItem(anyLong()))
                .thenReturn(item1);
        when(itemStorage.updateItem(any()))
                .thenReturn(item2);

        ItemDto itemDto = itemService.updateItem(1L, itemUpdateDto);

        assertThat(itemDto, notNullValue());
        assertThat(itemDto.getName(), equalTo(item2.getName()));
        assertThat(itemDto.getDescription(), equalTo(item2.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item2.isAvailable()));
    }

    @Test
    void testSearchItems() {
        List<ItemDto> userItems = itemService.searchItems(1L, "");

        assertThat(userItems, notNullValue());
        assertThat(userItems.size(), equalTo(0));

        userItems = itemService.searchItems(1L, null);

        assertThat(userItems, notNullValue());
        assertThat(userItems.size(), equalTo(0));

        when(itemStorage.searchItems(anyString()))
                .thenReturn(List.of(item1, item2));

        userItems = itemService.searchItems(1L, "asd");

        assertThat(userItems, notNullValue());
        assertThat(userItems.size(), equalTo(2));
    }

    @Test
    void testValidateItemId() {
        when(itemStorage.getItem(anyLong()))
                .thenReturn(item1);

        Item item = itemService.validateItemId(1L);

        assertThat(item, notNullValue());
        assertThat(item, equalTo(item1));
    }
}