package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemDto toItemDto(Item item, Long userId) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId());

        if (item.getComments() != null) {
            builder.comments((item.getComments().stream()
                    .map(CommentMapper::toDto)
                    .toList()));
        } else {
            builder.comments(List.of());
        }

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            builder.lastBooking(item.getLastBooking() != null ? BookingMapper.toDto(item.getLastBooking()) : null);
            builder.nextBooking(item.getNextBooking() != null ? BookingMapper.toDto(item.getNextBooking()) : null);
        }

        return builder.build();
    }

    public static Item toItem(ItemDto itemDto, User owner, Long requestId) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .requestId(requestId)
                .build();
    }

    public static Item updateItemFields(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }
}