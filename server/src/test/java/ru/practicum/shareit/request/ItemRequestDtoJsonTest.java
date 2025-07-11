package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.request.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serializeRequestDto() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(5L)
                .description("need drill")
                .created(LocalDateTime.of(2025, 6, 16, 13, 0))
                .build();

        var content = json.write(dto);

        assertThat(content).hasJsonPathNumberValue("$.id");
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("need drill");
        assertThat(content).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-06-16T13:00:00");
    }

    @Test
    void roundTripDeserializeRequestDto() throws Exception {
        ItemRequestDto original = ItemRequestDto.builder()
                .id(8L)
                .description("saw please")
                .created(LocalDateTime.of(2025, 6, 16, 14, 30))
                .build();

        String jsonString = json.write(original).getJson();
        ItemRequestDto parsed = json.parseObject(jsonString);

        assertThat(parsed.getId()).isEqualTo(original.getId());
        assertThat(parsed.getDescription()).isEqualTo(original.getDescription());
        assertThat(parsed.getCreated()).isEqualTo(original.getCreated());
    }
}