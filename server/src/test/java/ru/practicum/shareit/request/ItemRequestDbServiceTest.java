package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDbService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserDbService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ItemRequestDbService.class, UserDbService.class, ItemDbService.class})
class ItemRequestDbServiceTest {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;

    private static final UserDto userDto1 = new UserDto(null, "user1", "a@mail");
    private static final UserDto userDto2 = new UserDto(null, "user2", "b@mail");
    private static final ItemRequestDto itemRequestDto1 = new ItemRequestDto(null, "qwe", null, null);
    private static final ItemRequestDto itemRequestDto2 = new ItemRequestDto(null, "asd", null, null);

    @Autowired
    public ItemRequestDbServiceTest(ItemRequestService itemRequestService,
                                    @Qualifier("itemDbService") ItemService itemService,
                                    @Qualifier("userDbService") UserService userService) {
        this.itemRequestService = itemRequestService;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Test
    void testGetUserRequests() {
        UserDto user1 = userService.createUser(userDto1);
        UserDto user2 = userService.createUser(userDto2);

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(user1.getId());

        assertThat(requests, notNullValue());

        ItemRequestDto requestDto1 = itemRequestService.createRequest(user1.getId(), itemRequestDto1);
        ItemRequestDto requestDto2 = itemRequestService.createRequest(user1.getId(), itemRequestDto2);
        ItemDto itemDto = new ItemDto(null, "item1", "some item", true, null, requestDto1.getId());
        ItemDto newItem = itemService.createItem(user2.getId(), itemDto);
        RequestItemDto requestItemDto = new RequestItemDto(newItem.getId(), newItem.getName(), user2.getId());
        requestDto1.setItems(List.of(requestItemDto));
        requestDto2.setItems(List.of());


        requests = itemRequestService.getUserRequests(user1.getId());

        assertThat(requests, notNullValue());
        assertThat(requests.size(), equalTo(2));
        assertThat(requests, hasItems(requestDto1, requestDto2));
        assertThat(requests.getLast().getItems(), hasItem(requestItemDto));
    }

    @Test
    void testGetAllRequests() {
        UserDto user = userService.createUser(userDto1);

        List<ItemRequestDto> requests = itemRequestService.getAllRequests();

        assertThat(requests, notNullValue());

        ItemRequestDto requestDto1 = itemRequestService.createRequest(user.getId(), itemRequestDto1);
        ItemRequestDto requestDto2 = itemRequestService.createRequest(user.getId(), itemRequestDto2);

        requests = itemRequestService.getAllRequests();

        assertThat(requests, notNullValue());
        assertThat(requests.size(), equalTo(2));
        assertThat(requests, hasItems(requestDto1, requestDto2));
    }

    @Test
    void testGetRequest() {
        UserDto user = userService.createUser(userDto1);
        ItemRequestDto requestDto = itemRequestService.createRequest(user.getId(), itemRequestDto1);

        ItemRequestDto returnedRequest = itemRequestService.getRequest(requestDto.getId());

        assertThat(returnedRequest, notNullValue());
        assertThat(returnedRequest.getId(), equalTo(requestDto.getId()));
        assertThat(returnedRequest.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(returnedRequest.getCreated(), equalTo(requestDto.getCreated()));
    }

    @Test
    void testCreateRequest() {
        UserDto user = userService.createUser(userDto1);
        ItemRequestDto createdRequest = itemRequestService.createRequest(user.getId(), itemRequestDto1);

        assertThat(createdRequest, notNullValue());
        assertThat(createdRequest.getDescription(), equalTo(itemRequestDto1.getDescription()));
    }

    @Test
    void testValidateRequestId() {
        UserDto user = userService.createUser(userDto1);
        ItemRequestDto createdRequest = itemRequestService.createRequest(user.getId(), itemRequestDto1);
        ItemRequest validatedRequest = itemRequestService.validateRequestId(createdRequest.getId());

        assertThat(validatedRequest, notNullValue());
        assertThat(validatedRequest.getDescription(), equalTo(createdRequest.getDescription()));
        assertThat(validatedRequest.getCreated(), equalTo(createdRequest.getCreated()));
        assertThat(validatedRequest.getRequester(), notNullValue());
        assertThat(validatedRequest.getRequester().getName(), equalTo(user.getName()));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.validateRequestId(createdRequest.getId() + 1));
    }
}