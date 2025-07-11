package ru.practicum.shareit.request;

import ru.practicum.request.ItemRequestDto;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest req) {
        ItemRequestDto dto = new ItemRequestDto();

        dto.setId(req.getId());
        dto.setDescription(req.getDescription());
        dto.setCreated(req.getCreated());

        return dto;
    }
}
