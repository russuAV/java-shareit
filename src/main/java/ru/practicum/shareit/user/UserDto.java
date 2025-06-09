package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;

    @Email(message = "Некорректный email")
    private String email;
    private String name;
}