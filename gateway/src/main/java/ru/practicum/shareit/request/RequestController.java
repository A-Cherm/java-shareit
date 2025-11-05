package ru.practicum.shareit.request;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Запросы предметов", description = "Управление запросами")
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping
    @Operation(summary = "Получение запросов пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ItemRequestDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получить список запросов пользователя с id = {}", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    @Operation(summary = "Получение всех запросов",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ItemRequestDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получить список всех запросов");
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Получение запроса",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = ItemRequestDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long requestId) {
        log.info("Получить запрос с id = {}", requestId);
        return requestClient.getRequest(userId, requestId);
    }

    @PostMapping
    @Operation(summary = "Добавление запроса",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = ItemRequestDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("Создать запрос {}", requestDto);
        return requestClient.addRequest(userId, requestDto);
    }
}
