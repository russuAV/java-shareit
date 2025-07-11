package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.user.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void serializeUserDto() throws Exception {
        UserDto dto = UserDto.builder()
                .id(7L)
                .email("x@x.com")
                .name("X")
                .build();

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(7);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("x@x.com");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("X");
    }

    @Test
    void deserializeUserDto() throws Exception {
        UserDto original = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .name("T")
                .build();

        String jsonStr = json.write(original).getJson();
        UserDto parsed = json.parseObject(jsonStr);

        assertThat(parsed).isEqualTo(original);
    }
}