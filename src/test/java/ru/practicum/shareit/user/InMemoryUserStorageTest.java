package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserStorageTest {
    private UserStorage userStorage;
    private static final User user = new User(1L, "user", "user@mail", null);

    @BeforeEach
    public void newStorage() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void testGetUser() {
        userStorage.createUser(user);

        User newUser = userStorage.getUser(1);

        assertEquals(1, newUser.getId(), "Неверный id пользователя");
        assertEquals("user", newUser.getName(), "Неверное имя пользователя");
        assertEquals("user@mail", newUser.getEmail(), "Неверная почта пользователя");
    }

    @Test
    void testCreateUser() {
        User newUser = userStorage.createUser(user);

        assertEquals(1, newUser.getId(), "Неверный id пользователя");
        assertEquals("user", newUser.getName(), "Неверное имя пользователя");
        assertEquals("user@mail", newUser.getEmail(), "Неверная почта пользователя");
    }

    @Test
    void testUpdateUser() {
        userStorage.createUser(user);

        User newUser = new User(1L, "newUser", "new@mail", null);

        User updatedUser = userStorage.updateUser(newUser);

        assertEquals(1, updatedUser.getId(), "Неверный id пользователя");
        assertEquals("newUser", updatedUser.getName(), "Неверное имя пользователя");
        assertEquals("new@mail", updatedUser.getEmail(), "Неверная почта пользователя");
    }

    @Test
    void testDeleteUser() {
        userStorage.createUser(user);

        userStorage.deleteUser(1);

        assertThrows(NotFoundException.class, () -> userStorage.getUser(1));
    }
}