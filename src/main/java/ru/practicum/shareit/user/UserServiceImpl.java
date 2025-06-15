package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("Ошибка создания пользователя: email {} уже используется", userDto.getEmail());
            throw new ConflictException("Данный email уже используется");
        }

        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.save(user);

        log.info("Пользователь успешно создан с id: {}, email: {}",
                user.getId(), user.getEmail());
        return createdUser;
    }

    @Override
    public User update(UserDto userDto) {
        if (userDto.getId() == null || userDto.getId() == 0) {
            throw new ValidationException("id должен быть указан");
        }

        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null && !Objects.equals(userDto.getEmail(), existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                log.error("Ошибка обновления: email {} уже используется", userDto.getEmail());
                throw new ConflictException("Этот e-mail уже используется");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        UserMapper.updateUserFields(existingUser, userDto);
        User updatedUser = userRepository.save(existingUser);

        log.info("Пользователь с id {} успешно обновлён", updatedUser.getId());
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден.");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long userId) {
        User user = getUserById(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id {} удалён", userId);
    }
}