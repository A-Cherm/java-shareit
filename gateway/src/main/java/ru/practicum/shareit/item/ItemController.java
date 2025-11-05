package ru.practicum.shareit.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Предметы", description = "Управление предметами")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    @Operation(summary = "Получение предметов пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ItemDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Данные бронирования доступны только автору "
                            + "или владельцу вещи", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет бронирования с данным id", content = @Content)
            })
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получить вещи пользователя с id = {}", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Получение предмета",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = ItemDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long itemId) {
        log.info("Получить вещь с id = {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    @Operation(summary = "Создание предмета",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = ItemDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content)
            })
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Создать вещь с id = {}", itemDto);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Operation(summary = "Обновление предмета",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = ItemDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет предмета с данным id", content = @Content)
            })
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @Valid @RequestBody ItemUpdateDto itemDto) {
        log.info("Обновить вещь с id = {}: {}", itemId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск предмета по строке в названии или описании",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ItemDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет предмета с данным id", content = @Content)
            })
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam(name = "text") String searchQuery) {
        log.info("Поиск вещей по запросу {}", searchQuery);
        if (searchQuery == null || searchQuery.isBlank()) {
            return ResponseEntity.status(HttpStatus.OK).body(List.of());
        }
        return itemClient.searchItems(userId, searchQuery);
    }

    @PostMapping("/{itemId}/comment")
    @Operation(summary = "Добавить комментарий к предмету",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = CommentDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет предмета с данным id", content = @Content)
            })
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавить комментарий {}", commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
