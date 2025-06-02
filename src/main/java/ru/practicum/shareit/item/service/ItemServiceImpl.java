package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong itemIdCounter = new AtomicLong(0);

    @Override
    public Item create(Long userId, ItemDto itemDto) {
        User owner = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, owner, null); // пока request == null
        item.setId(getNextId());
        items.put(item.getId(), item);

        log.info("Вещь создана: {}", item);
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = getItemById(itemId);
        if (!Objects.equals(item.getOwnerId(), userId)) {
            throw new NotFoundException("Редактировать может только владелец.");
        }

        Item update = ItemMapper.updateItemFields(item, itemDto);
        log.info("Вещь обновлена: {}", item);
        return update;
    }

    @Override
    public Item getItemById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            log.error("Вещь с id {} не найдена.", itemId);
            throw new NotFoundException("Вещь с id " + itemId + " не найдена.");
        }

        log.info("Вещь с id {} получена.", itemId);
        return item;
    }

    @Override
    public List<Item> getAllByOwner(Long userId) {
        List<Item> itemsByOwner = items.values().stream()
                .filter(i -> i.getOwnerId().equals(userId))
                .toList();

        log.info("Получен список всех вещей, сдаваемых пользователем с ID {}", userId);
        return itemsByOwner;
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            log.info("Пустой запрос для поиска");
            return List.of();
        }
        return findAvailableItemsByText(text);
    }

    public List<Item> findAvailableItemsByText(String text) {
        String lower = text.toLowerCase();
        return items.values().stream()
                .filter(i -> Boolean.TRUE.equals(i.getAvailable()))
                .filter(i -> i.getName().toLowerCase().contains(lower) ||
                        i.getDescription().toLowerCase().contains(lower))
                .toList();
    }

    public long getNextId() {
        return itemIdCounter.incrementAndGet();
    }
}