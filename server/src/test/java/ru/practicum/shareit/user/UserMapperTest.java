package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.user.UserDto;

import static org.assertj.core.api.Assertions.*;

class UserMapperTest {
    @Test
    void toDto() {
        User u = new User(1L,"a@b.c","A");
        UserDto dto = UserMapper.toUserDto(u);

        assertThat(dto.getEmail()).isEqualTo("a@b.c");
        assertThat(dto.getName()).isEqualTo("A");
    }
}