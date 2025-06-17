package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        UserDto userDto = userService.getUser(id);

        log.info("Возвращается пользователь {}", userDto);
        return userDto;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        UserDto user = userService.createUser(userDto);

        log.info("Создан пользователь {}", user);
        return user;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UserUpdateDto userDto,
                              @PathVariable long id) {
        userDto.setId(id);
        UserDto user = userService.updateUser(userDto);

        log.info("Обновлён пользователь {}", user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        log.info("Удалён пользователь с id = {}", id);
    }
}
