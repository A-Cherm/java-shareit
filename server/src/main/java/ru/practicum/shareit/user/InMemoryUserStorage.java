package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public User getUser(long id) {
        validateId(id);
        return users.get(id);
    }

    public User createUser(User user) {
        validateEmail(user.getEmail());
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public void deleteUser(long id) {
        users.remove(id);
    }

    public void validateId(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Нет пользователя с id = " + id);
        }
    }

    public void validateEmail(String email) {
        if (users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(email::equals)) {
            throw new DataConflictException("Есть пользователь с почтой " + email);
        }
    }

    private long getNextId() {
        long maxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
