package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemRequestDbService implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemRequestDbService(ItemRequestRepository itemRequestRepository, ItemRepository itemRepository,
                                @Qualifier("userDbService") UserService userService) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        Set<Long> requestIds = itemRequests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());
        List<RequestItem> items = itemRepository.findAllByRequestIdIn(requestIds);
        Map<Long, List<RequestItemDto>> itemsByRequestId = new HashMap<>();

        requestIds.forEach(id -> itemsByRequestId.put(id, new ArrayList<>()));
        for (RequestItem item : items) {
            itemsByRequestId.get(item.getRequest().getId()).add(ItemMapper.mapToRequestItemDto(item));
        }

        return itemRequests
                .stream()
                .map(request -> ItemRequestMapper.mapToItemRequestDto(request,
                        itemsByRequestId.get(request.getId())))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        return itemRequestRepository.findAllByOrderByCreatedDesc()
                .stream()
                .map(request -> ItemRequestMapper.mapToItemRequestDto(request, null))
                .toList();
    }

    @Override
    public ItemRequestDto getRequest(long requestId) {
        ItemRequest itemRequest = validateRequestId(requestId);
        List<RequestItemDto> items = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapToRequestItemDto)
                .toList();

        return ItemRequestMapper.mapToItemRequestDto(itemRequest, items);
    }

    @Override
    @Transactional
    public ItemRequestDto createRequest(long userId, ItemRequestDto requestDto) {
        User user = userService.validateUserId(userId);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(requestDto, user);

        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, null);
    }

    @Override
    public ItemRequest validateRequestId(long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет запроса с id = " + id));
    }
}
