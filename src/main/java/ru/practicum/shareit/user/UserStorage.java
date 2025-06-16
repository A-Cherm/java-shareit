package ru.practicum.shareit.user;

public interface UserStorage {
    User getUser(long id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

    void addItem(long userId, long itemId);

    void validateId(long id);

    void validateEmail(String email);
}
