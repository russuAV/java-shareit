package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingStatus;
import ru.practicum.item.ItemDto;
import ru.practicum.item.comment.CommentDto;
import ru.practicum.item.NewItemRequest;
import ru.practicum.item.UpdateItemRequest;
import ru.practicum.item.comment.NewCommentRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
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
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, NewItemRequest newItemRequest) {
        User owner = userService.getEntityById(userId);
        Long requestId = newItemRequest.getRequestId();

        if (requestId != null) {
            if (!itemRequestRepository.existsById(requestId)) {
                throw new NotFoundException("Ведь с идентификатором запроса " + requestId + " не найдена.");
            }
        }

        Item item = ItemMapper.toItem(newItemRequest, owner, requestId);
        Item savedItem = itemRepository.save(item);

        log.info("Вещь создана: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        Item item = getEntityById(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Редактировать может только владелец.");
        }

        ItemMapper.updateItemFields(item, updateItemRequest);
        log.info("Вещь обновлена: {}", item);
        itemRepository.save(item);

        return ItemMapper.toItemDto(item);
    }


    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId, Long requesterId) {
        Item item = getEntityById(itemId);

        if (Objects.equals(item.getOwner().getId(), requesterId)) {
            item.setLastBooking(bookingRepository.findLastBooking(itemId, LocalDateTime.now()));
            item.setNextBooking(bookingRepository.findNextBooking(itemId, LocalDateTime.now()));
        }

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        item.setComments(comments);

        return ItemMapper.toItemDto(item, requesterId);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getEntityById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllByOwner(Long userId) {
        userService.getUserById(userId);
        List<Item> itemsByOwner = itemRepository.findAllByOwnerId(userId);
        log.info("Получен список всех вещей, сдаваемых пользователем с ID {}", userId);
        return itemsByOwner.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            log.info("Пустой запрос для поиска");
            return List.of();
        }
        List<Item> items = itemRepository.search(text.toLowerCase());

        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentRequest newCommentRequest) {
        Item item = getEntityById(itemId);
        User author = userService.getEntityById(userId);

        // Проверяем есть ли хотя бы одно завершённое бронирование этой вещи этим пользователем
        boolean hasUsedItem = bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(
                itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (!hasUsedItem) {
            throw new ValidationException("Пользователь не брал эту вещь или бронирование не завершено");
        }

        Comment comment = Comment.builder()
                .message(newCommentRequest.getText())
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        Comment saved = commentRepository.save(comment);

        return CommentMapper.toDto(saved);
    }
}