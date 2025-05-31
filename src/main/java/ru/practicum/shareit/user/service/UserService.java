package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User create(UserDto userDto);

    User update(UserDto userDto);

    User getUserById(Long id);

    List<User> getAll();

    void delete(Long id);
}