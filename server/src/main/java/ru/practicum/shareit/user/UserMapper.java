package ru.practicum.shareit.user;

import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UpdateUserRequest;
import ru.practicum.user.UserDto;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static void updateUserFields(User user, UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.getName() != null) {
            user.setName(updateUserRequest.getName());
        }
    }
}