package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exception.ValidationException;

import java.util.Optional;

public enum BookingState {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }

    public static BookingState validateState(String stringState) {
        return BookingState.from(stringState)
                .orElseThrow(() -> new ValidationException("Неизвестный статус: " + stringState));
    }
}