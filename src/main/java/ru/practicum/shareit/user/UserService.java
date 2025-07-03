package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public interface UserService {
    UserDto getUser(long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserUpdateDto userDto);

    void deleteUser(long id);

    User validateUserId(long id);
}
