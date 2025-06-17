package ru.practicum.shareit.user;

import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UpdateUserRequest;
import ru.practicum.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest newUserRequest);

    UserDto update(UpdateUserRequest updateUserRequest);

    UserDto getUserById(Long id);

    User getEntityById(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}