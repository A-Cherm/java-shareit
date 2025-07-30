package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getRequestsFromOtherUsers(long userId);

    ItemRequestDto getRequest(long requestId);

    ItemRequestDto createRequest(long userId, ItemRequestDto requestDto);

    ItemRequest validateRequestId(long id);
}
