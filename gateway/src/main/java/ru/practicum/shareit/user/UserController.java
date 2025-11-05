package ru.practicum.shareit.user;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пользователи", description = "Управление пользователями")
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{id}")
    @Operation(summary = "Получение пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        log.info("Получить пользователя с id = {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    @Operation(summary = "Создание пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content)
            })
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Создать пользователя {}", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК",
                            content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> updateUser(@PathVariable long id,
                                             @Valid @RequestBody UserUpdateDto userDto) {
        log.info("Обновить пользователя с id = {}: {}", id, userDto);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "ОК", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Нет пользователя с данным id", content = @Content)
            })
    public ResponseEntity<Object> deleteUser(@PathVariable long id) {
        log.info("Удалить пользователя с id = {}", id);
        return userClient.deleteUser(id);
    }
}
