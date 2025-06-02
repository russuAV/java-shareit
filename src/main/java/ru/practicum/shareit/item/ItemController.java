package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto dto) {
        return ItemMapper.toItemDto(itemService.update(userId, itemId, dto));
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(USER_HEADER) Long userId) {
        return itemService.getAllByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}