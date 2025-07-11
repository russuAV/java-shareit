package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UpdateUserRequest;
import ru.practicum.user.UserDto;

import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            log.error("Ошибка создания пользователя: email {} уже используется", newUserRequest.getEmail());
            throw new ConflictException("Данный email уже используется");
        }

        User user = UserMapper.toUser(newUserRequest);
        User createdUser = userRepository.save(user);

        log.info("Пользователь успешно создан с id: {}, email: {}",
                user.getId(), user.getEmail());
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto update(UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.getId() == null || updateUserRequest.getId() == 0) {
            throw new ValidationException("id должен быть указан");
        }

        User existingUser = userRepository.findById(updateUserRequest.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (updateUserRequest.getEmail() != null && !Objects.equals(updateUserRequest.getEmail(), existingUser.getEmail())) {
            if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                log.error("Ошибка обновления: email {} уже используется", updateUserRequest.getEmail());
                throw new ConflictException("Этот e-mail уже используется");
            }
            existingUser.setEmail(updateUserRequest.getEmail());
        }

        UserMapper.updateUserFields(existingUser, updateUserRequest);
        User updatedUser = userRepository.save(existingUser);

        log.info("Пользователь с id {} успешно обновлён", updatedUser.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", userId);
                    return new NotFoundException("Пользователь с id " + userId + " не найден.");
                });
        return UserMapper.toUserDto(user);
    }

    @Override
    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void delete(Long userId) {
        getEntityById(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id {} удалён", userId);
    }
}