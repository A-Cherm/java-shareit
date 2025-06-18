package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public UserDto getUser(long id) {
        User user = userStorage.getUser(id);

        return UserMapper.mapToUserDto(user);
    }

    public UserDto createUser(UserDto userDto) {
        User user = userStorage.createUser(UserMapper.mapToUser(userDto));

        return UserMapper.mapToUserDto((user));
    }

    public UserDto updateUser(UserUpdateDto userDto) {
        User oldUser = userStorage.getUser(userDto.getId());

        log.debug("Исходные данные пользователя: {}", oldUser);
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(oldUser.getEmail())) {
            userStorage.validateEmail(userDto.getEmail());
            oldUser.setEmail(userDto.getEmail());
        }
        log.debug("Обновлённые данные пользователя: {}", oldUser);

        oldUser = userStorage.updateUser(oldUser);

        return UserMapper.mapToUserDto(oldUser);
    }

    public void deleteUser(long id) {
        User user = userStorage.getUser(id);

        user.getItemIds().forEach(itemStorage::deleteItem);
        userStorage.deleteUser(id);
    }
}
