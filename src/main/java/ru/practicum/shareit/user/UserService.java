package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User create(UserDto userDto);

    User update(UserDto userDto);

    User getUserById(Long id);

    List<User> getAll();

    void delete(Long id);
}