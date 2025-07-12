package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemRequestDto> itemRequests = itemRequestService.getUserRequests(userId);

        log.info("Список запросов пользователя с id = {}: {}", userId, itemRequests);
        return itemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests() {
        List<ItemRequestDto> itemRequests = itemRequestService.getAllRequests();

        log.info("Список всех запросов: {}", itemRequests);
        return itemRequests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable long requestId) {
        ItemRequestDto request = itemRequestService.getRequest(requestId);

        log.info("Возвращается запрос {}", request);
        return request;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody ItemRequestDto requestDto) {
        ItemRequestDto request = itemRequestService.createRequest(userId, requestDto);

        log.info("Создан запрос {}", request);
        return request;
    }
}
