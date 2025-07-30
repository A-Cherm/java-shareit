package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.InMemoryItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    private UserService userService;

    private final InMemoryItemStorage itemStorage = Mockito.mock(InMemoryItemStorage.class);
    private final InMemoryUserStorage userStorage = Mockito.mock(InMemoryUserStorage.class);

    private final User user1 = new User(1L, "user1", "a@mail");
    private final UserDto userDto = new UserDto(1L, "user1", "a@mail");

    @BeforeEach
    void newService() {
        this.userService = new UserServiceImpl(userStorage, itemStorage);
    }

    @Test
    void testGetUser() {
        when(userStorage.getUser(anyLong()))
                .thenReturn(user1);

        UserDto userDto = userService.getUser(1L);

        assertThat(userDto.getName(), equalTo(user1.getName()));
        assertThat(userDto.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void testCreateUser() {
        when(userStorage.createUser(any()))
                .thenReturn(user1);

        UserDto newUser = userService.createUser(userDto);

        assertThat(newUser.getName(), equalTo(user1.getName()));
        assertThat(newUser.getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void testUpdateUser() {
        UserUpdateDto updateDto1 = new UserUpdateDto(1L, null, "abc@mail");
        UserUpdateDto updateDto2 = new UserUpdateDto(1L, "aaa", null);
        User updatedUser1 = new User(1L, user1.getName(), updateDto1.getEmail());
        User updatedUser2 = new User(1L, updateDto2.getName(), user1.getEmail());

        when(userStorage.getUser(anyLong()))
                .thenReturn(user1);
        when(userStorage.updateUser(any()))
                .thenReturn(updatedUser1);

        UserDto updatedUser = userService.updateUser(updateDto1);

        assertThat(updatedUser.getName(), equalTo(updatedUser1.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updatedUser1.getEmail()));

        updatedUser = userService.updateUser(updateDto1);

        assertThat(updatedUser.getName(), equalTo(updatedUser1.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updatedUser1.getEmail()));

        when(userStorage.updateUser(any()))
                .thenReturn(updatedUser2);

        updatedUser = userService.updateUser(updateDto2);

        assertThat(updatedUser.getName(), equalTo(updatedUser2.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updatedUser2.getEmail()));
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);

        Mockito.verify(userStorage, Mockito.times(1))
                .deleteUser(1L);
        Mockito.verify(itemStorage, Mockito.times(1))
                .deleteUserItems(1L);
    }

    @Test
    void testValidateUserId() {
        when(userStorage.getUser(anyLong()))
                .thenThrow(new NotFoundException(""));

        assertThrows(NotFoundException.class,
                () -> userService.validateUserId(1L));
    }
}