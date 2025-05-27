package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();

    @Override
    public User create(UserDto userDto) {
        if (users.containsKey(userDto.getId())) {
            log.error("Ошибка создания пользователя: id {} уже используется", userDto.getId());
            throw new ValidationException("Пользователь уже зарегистрирован");
        }
        if (usersByEmail.containsKey(userDto.getEmail())) {
            log.error("Ошибка создания пользователя: email {} уже используется", userDto.getEmail());
            throw new ValidationException("Данный email уже используется");
        }

        Long id = getNextId();
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        users.put(id, user);
        usersByEmail.put(user.getEmail(), user);

        log.info("Пользователь успешно создан с id: {}, email: {}",
                user.getId(), user.getEmail());
        return user;
    }

    @Override
    public User update(UserDto userDto) {
        if (userDto.getId() == null || userDto.getId() == 0) {
            throw new ValidationException("id должен быть указан");
        }
        User userWithOldData = users.get(userDto.getId());
        if (userWithOldData == null) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        // проверяем доступность email
        if (userDto.getEmail() != null
                && !userDto.getEmail().equals(userWithOldData.getEmail())) {
            if (usersByEmail.containsKey(userDto.getEmail())) {
                User existingUserByEmail = usersByEmail.get(userDto.getEmail());
                if (!existingUserByEmail.getId().equals(userDto.getId())) {
                    log.error("Ошибка обновления: email {} уже используется", userDto.getEmail());
                    throw new ValidationException("Этот e-mail уже используется");
                }
            }
        }

        User updateUser = UserMapper.updateUserFields(userWithOldData, userDto);

        // удаляем старого пользователя и возвращаем нового
        users.remove(userWithOldData.getId());
        usersByEmail.remove(userWithOldData.getEmail());
        users.put(userDto.getId(), updateUser);
        usersByEmail.put(userDto.getEmail(), updateUser);

        log.info("Пользователь с id {} успешно обновлён", userWithOldData.getId());
        return updateUser;
    }

    @Override
    public User getUserById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            log.error("Пользователь с id {} не найден.", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }

        log.info("Пользователь с id {} получен.", userId);
        return user;
    }

    @Override
    public List<User> getAll() {
        return users.values().stream()
                .toList();
    }

    @Override
    public void delete(Long userId) {
        User user = getUserById(userId); // один вызов
        users.remove(userId);
        usersByEmail.remove(user.getEmail());
        log.info("Пользователь с id {} удален", userId);
    }

    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}