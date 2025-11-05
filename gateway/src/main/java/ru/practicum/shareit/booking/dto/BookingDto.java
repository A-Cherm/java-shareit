package ru.practicum.shareit.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сущность бронирования")
public class BookingDto {
    @NotNull
    @Schema(description = "Id вещи", example = "1")
    private Long itemId;
    @NotNull
    @FutureOrPresent
    @Schema(description = "Дата начала брони", example = "2000-01-01T00:00:00", type = "string")
    private LocalDateTime start;
    @NotNull
    @Future
    @Schema(description = "Дата конца брони", example = "2001-01-01T00:00:00", type = "string")
    private LocalDateTime end;

    public void validateTimeFrame() {
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Некорректный интервал бронирования");
        }
    }
}