package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.item.ItemDto;

import static org.assertj.core.api.Assertions.*;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serializeDeserialize() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(5L).name("drill").description("tool").available(true).build();

        String j = json.write(dto).getJson();
        assertThat(j).contains("\"name\":\"drill\"");
        assertThat(json.parseObject(j).getAvailable()).isTrue();
    }
}