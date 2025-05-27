package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;

    @Email(message = "Некорректный email")
    @NotNull
    private String email;

    private String name;
}