package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item create(Long userId, ItemDto itemDto) {
        User owner = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, owner, null); // пока request == null
        Item savedItem = itemRepository.save(item);

        log.info("Вещь создана: {}", item);
        return savedItem;
    }

    @Override
    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена."));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Редактировать может только владелец.");
        }

        Item updatedItem = ItemMapper.updateItemFields(item, itemDto);
        log.info("Вещь обновлена: {}", item);
        return itemRepository.save(updatedItem);
    }


    @Override
    @Transactional(readOnly = true)
    public Item getItemById(Long itemId, Long requesterId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена."));

        if (Objects.equals(item.getOwner().getId(), requesterId)) {
            item.setLastBooking(bookingRepository.findLastBooking(itemId, LocalDateTime.now()));
            item.setNextBooking(bookingRepository.findNextBooking(itemId, LocalDateTime.now()));
        }

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        item.setComments(comments);

        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getAllByOwner(Long userId) {
        userService.getUserById(userId);
        List<Item> itemsByOwner = itemRepository.findAllByOwnerId(userId);
        log.info("Получен список всех вещей, сдаваемых пользователем с ID {}", userId);
        return itemsByOwner;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            log.info("Пустой запрос для поиска");
            return List.of();
        }
        return itemRepository.search(text.toLowerCase());
    }

    @Override
    public Comment addComment(Long userId, Long itemId, CommentDto dto) {
        Item item = getItemById(itemId, userId);
        User author = userService.getUserById(userId);

        // Проверяем есть ли хотя бы одно завершённое бронирование этой вещи этим пользователем
        boolean hasUsedItem = bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(
                itemId, userId, LocalDateTime.now(), Booking.BookingStatus.APPROVED);

        if (!hasUsedItem) {
            throw new ValidationException("Пользователь не брал эту вещь или бронирование не завершено");
        }

        Comment comment = Comment.builder()
                .message(dto.getText())
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }
}