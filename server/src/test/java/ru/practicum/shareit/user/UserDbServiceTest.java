package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserDbService.class)
class UserDbServiceTest {
    private final UserService userService;

    private final UserDto userDto = new UserDto(null, "user", "user@mail");

    @Autowired
    UserDbServiceTest(@Qualifier("userDbService") UserService userService) {
        this.userService = userService;
    }

    @Test
    void testGetUser() {
        UserDto createdUser = userService.createUser(userDto);
        UserDto returnedUser = userService.getUser(createdUser.getId());

        assertNotNull(returnedUser, "Пользователь не возвращается");
        assertEquals(createdUser.getName(), returnedUser.getName(), "Неверное имя пользователя");
        assertEquals(createdUser.getEmail(), returnedUser.getEmail(), "Неверная почта пользователя");
    }

    @Test
    void testCreateUser() {
        UserDto createdUser = userService.createUser(userDto);

        assertNotNull(createdUser, "Пользователь не возвращается");
        assertEquals(userDto.getName(), createdUser.getName(), "Неверное имя пользователя");
        assertEquals(userDto.getEmail(), createdUser.getEmail(), "Неверная почта пользователя");
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        userService.createUser(userDto);

        assertThrows(DataConflictException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testUpdateUser() {
        UserDto createdUser = userService.createUser(userDto);
        UserUpdateDto update1 = new UserUpdateDto(createdUser.getId(), "new name", null);
        UserDto updatedUser1 = userService.updateUser(update1);

        assertNotNull(updatedUser1, "Пользователь не возвращается");
        assertEquals(update1.getName(), updatedUser1.getName(), "Неверное имя пользователя");
        assertEquals(createdUser.getEmail(), updatedUser1.getEmail(), "Неверная почта пользователя");

        UserUpdateDto update2 = new UserUpdateDto(createdUser.getId(), null, "new@yandex");
        UserDto updatedUser2 = userService.updateUser(update2);

        assertNotNull(updatedUser2, "Пользователь не возвращается");
        assertEquals(updatedUser1.getName(), updatedUser2.getName(), "Неверное имя пользователя");
        assertEquals(update2.getEmail(), updatedUser2.getEmail(), "Неверная почта пользователя");
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        UserDto createdUser1 = userService.createUser(userDto);
        UserDto userDto2 = new UserDto(null, "name", "a@mail");
        userService.createUser(userDto2);
        UserUpdateDto update1 = new UserUpdateDto(createdUser1.getId(), "new name", createdUser1.getEmail());
        UserDto updatedUser = userService.updateUser(update1);
        assertNotNull(updatedUser, "Пользователь не возвращается");
        assertEquals(update1.getName(), updatedUser.getName(), "Неверное имя пользователя");
        assertEquals(createdUser1.getEmail(), updatedUser.getEmail(), "Неверная почта пользователя");

        UserUpdateDto update2 = new UserUpdateDto(createdUser1.getId(), "new name", "a@mail");

        assertThrows(DataConflictException.class, () -> userService.updateUser(update2));
    }

    @Test
    void testDeleteUser() {
        UserDto createdUser = userService.createUser(userDto);
        userService.deleteUser(createdUser.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(createdUser.getId()),
                "Пользователь не удалён");
    }

    @Test
    void testValidateUserId() {
        UserDto createdUser = userService.createUser(userDto);
        User validatedUser = userService.validateUserId(createdUser.getId());

        assertNotNull(validatedUser, "Пользователь не возвращается");
        assertEquals(createdUser.getName(), validatedUser.getName(), "Неверное имя пользователя");
        assertEquals(createdUser.getEmail(), validatedUser.getEmail(), "Неверная почта пользователя");

        assertThrows(NotFoundException.class, () -> userService.validateUserId(createdUser.getId() + 10),
                "Возвращается пользователь по несуществующему id");
    }
}