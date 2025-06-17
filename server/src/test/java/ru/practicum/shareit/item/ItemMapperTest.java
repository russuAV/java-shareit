package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.item.ItemDto;
import ru.practicum.item.NewItemRequest;
import ru.practicum.item.UpdateItemRequest;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto_shouldMapFields() {
        Item item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful")
                .available(true)
                .requestId(5L)
                .build();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Drill");
        assertThat(dto.getDescription()).isEqualTo("Powerful");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(5L);
    }

    @Test
    void toShortItemDto_shouldMapFields() {
        Item item = Item.builder()
                .id(2L)
                .name("Saw")
                .requestId(7L)
                .build();

        ItemShortDto shortDto = ItemMapper.toShortItemDto(item);

        assertThat(shortDto.getId()).isEqualTo(2L);
        assertThat(shortDto.getName()).isEqualTo("Saw");
        assertThat(shortDto.getRequestId()).isEqualTo(7L);
    }

    @Test
    void toItem_shouldMapNewItemRequest() {
        NewItemRequest request = NewItemRequest.builder()
                .name("Hammer")
                .description("Metal hammer")
                .available(true)
                .build();

        User owner = User.builder().id(1L).name("Alex").build();

        Item item = ItemMapper.toItem(request, owner, 10L);

        assertThat(item.getName()).isEqualTo("Hammer");
        assertThat(item.getDescription()).isEqualTo("Metal hammer");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(owner);
        assertThat(item.getRequestId()).isEqualTo(10L);
    }

    @Test
    void updateItemFields_shouldUpdateOnlyNotNullFields() {
        Item item = Item.builder()
                .name("Old name")
                .description("Old desc")
                .available(true)
                .build();

        UpdateItemRequest update = UpdateItemRequest.builder()
                .name("New name")
                .description(null)  // should not update
                .available(false)
                .build();

        ItemMapper.updateItemFields(item, update);

        assertThat(item.getName()).isEqualTo("New name");
        assertThat(item.getDescription()).isEqualTo("Old desc");
        assertThat(item.getAvailable()).isFalse();
    }
}