package ru.practicum.shareit.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Обновление пользователя")
public class UserUpdateDto {
    @Schema(description = "Имя пользователя", example = "Вася")
    private String name;
    @Email
    @Schema(description = "Почта пользователя", example = "vasya@mail.com")
    private String email;
}