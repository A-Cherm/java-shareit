package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service("userDbService")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class UserDbService implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id = " + id));

        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        User user = userRepository.save(UserMapper.mapToUser(userDto));

        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserUpdateDto userDto) {
        User oldUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id = " + userDto.getId()));

        log.debug("Исходные данные пользователя: {}", oldUser);
        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(oldUser.getEmail())) {
            validateEmail(userDto.getEmail());
            oldUser.setEmail(userDto.getEmail());
        }
        log.debug("Обновлённые данные пользователя: {}", oldUser);

        oldUser = userRepository.save(oldUser);

        return UserMapper.mapToUserDto(oldUser);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void validateUserId(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Нет пользователя с id = " + id);
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DataConflictException("Есть пользователь с почтой " + email);
        }
    }
}
