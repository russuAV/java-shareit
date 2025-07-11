package ru.practicum.shareit.item;

import ru.practicum.item.ItemDto;
import ru.practicum.item.NewItemRequest;
import ru.practicum.item.UpdateItemRequest;
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

    public static ItemShortDto toShortItemDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
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

    public static Item toItem(NewItemRequest newItemRequest, User owner, Long requestId) {
        return Item.builder()
                .name(newItemRequest.getName())
                .description(newItemRequest.getDescription())
                .available(newItemRequest.getAvailable())
                .owner(owner)
                .requestId(requestId)
                .build();
    }

    public static Item updateItemFields(Item item, UpdateItemRequest updateItemRequest) {
        if (updateItemRequest.getName() != null) {
            item.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.getDescription() != null) {
            item.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.getAvailable() != null) {
            item.setAvailable(updateItemRequest.getAvailable());
        }
        return item;
    }
}