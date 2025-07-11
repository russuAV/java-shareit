package ru.practicum.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UpdateUserRequest {
    private Long id;
    private String name;

    @Email(message = "Некорректный email")
    private String email;
}