package ru.practicum.shareit.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Сущность пользователя")
public class UserDto {
    @NotBlank
    @Schema(description = "Имя пользователя", example = "Вася")
    private String name;
    @Email
    @NotBlank
    @Schema(description = "Почта пользователя", example = "vasya@mail.com")
    private String email;
}
