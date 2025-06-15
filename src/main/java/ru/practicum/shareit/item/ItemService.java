package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

public interface ItemService {
    Item create(Long userId, ItemDto itemDto);

    Item update(Long userId, Long itemId, ItemDto itemDto);

    Item getItemById(Long itemId, Long requesterId);

    List<Item> getAllByOwner(Long userId);

    List<Item> search(String text);

    Comment addComment(Long userId, Long itemId, CommentDto dto);
}