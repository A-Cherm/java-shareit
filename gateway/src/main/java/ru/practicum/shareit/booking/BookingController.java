package ru.practicum.shareit.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Бронирования предметов", description = "Управление бронированиями")
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    @Operation(summary = "Получение бронирования",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = BookingDto.class))),
                    @ApiResponse(responseCode = "400", description = "Данные бронирования доступны только автору "
                            + "или владельцу вещи", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет бронирования с данным id", content = @Content)
            })
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получить бронирование {}", bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PostMapping
    @Operation(summary = "Создание бронирования",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = BookingDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content)
            })
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookingDto bookingDto) {
        bookingDto.validateTimeFrame();
        log.info("Создать бронирование {}", bookingDto);
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @Operation(summary = "Обновление бронирования",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = BookingDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет бронирования с данным id", content = @Content),
                    @ApiResponse(responseCode = "409",
                            description = "Статус может менять только владелец вещи", content = @Content)
            })
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long bookingId,
                                    @RequestParam boolean approved) {
        log.info("Обновить бронирование {}: {}", bookingId, approved);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping
    @Operation(summary = "Получение бронирований пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") @Parameter(description = "Статус бронирования") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        BookingState state = BookingState.validateState(stateParam);

        log.info("Получить бронирования от пользователя {}, state={}, from={}, size={}", userId, stateParam, from, size);
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @Operation(summary = "Получение бронирований для вещей пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> getBookingsForItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") @Parameter(description = "Статус бронирования") String stateParam
    ) {
        BookingState state = BookingState.validateState(stateParam);

        log.info("Получить бронирования для вещей пользователя {}, state={}", userId, stateParam);
        return bookingClient.getBookingsForItems(userId, state);
    }
}