package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final AtomicLong userIdCounter = new AtomicLong(0);

    @Override
    public User create(UserDto userDto) {
        if (usersByEmail.containsKey(userDto.getEmail())) {
            log.error("Ошибка создания пользователя: email {} уже используется", userDto.getEmail());
            throw new ConflictException("Данный email уже используется");
        }

        User user = UserMapper.toUser(userDto);
        user.setId(getNextId());
        users.put(user.getId(), user);
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
        User existingUser = users.get(userDto.getId());
        if (existingUser == null) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        // проверяем доступность email
        if (!Objects.equals(userDto.getEmail(), existingUser.getEmail())) {
            if (usersByEmail.containsKey(userDto.getEmail())) {
                User existingUserByEmail = usersByEmail.get(userDto.getEmail());
                if (!Objects.equals(existingUserByEmail.getId(), userDto.getId())) {
                    log.error("Ошибка обновления: email {} уже используется", userDto.getEmail());
                    throw new ConflictException("Этот e-mail уже используется");
                }
            }
            usersByEmail.remove(existingUser.getEmail());
            existingUser.setEmail(userDto.getEmail());
            usersByEmail.put(existingUser.getEmail(), existingUser);
        }

        // Обновляем только изменяемые поля
        UserMapper.updateUserFields(existingUser, userDto);

        log.info("Пользователь с id {} успешно обновлён", existingUser.getId());
        return existingUser;
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
        return userIdCounter.incrementAndGet();
    }
}