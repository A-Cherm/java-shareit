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
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final ItemStorage itemStorage;

    public UserDto getUser(long id) {
        User user = userStorage.getUser(id);

        log.info("Возвращается пользователь {}", user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto createUser(UserDto userDto) {
        User user = userStorage.createUser(UserMapper.mapToUser(userDto));

        log.info("Создан пользователь {}", user);
        return UserMapper.mapToUserDto((user));
    }

    public UserDto updateUser(UserUpdateDto userDto) {
        userStorage.validateId(userDto.getId());

        User oldUser = userStorage.getUser(userDto.getId());

        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userStorage.validateEmail(userDto.getEmail());
            oldUser.setEmail(userDto.getEmail());
        }

        oldUser = userStorage.updateUser(oldUser);

        log.info("Обновлён пользователь {}", oldUser);
        return UserMapper.mapToUserDto(oldUser);
    }

    public void deleteUser(long id) {
        User user = userStorage.getUser(id);

        user.getItemIds().forEach(itemStorage::deleteItem);
        userStorage.deleteUser(id);
        log.info("Удалён пользователь {}", user);
    }
}
