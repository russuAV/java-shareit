package ru.practicum.shareit.item;

import ru.practicum.item.ItemDto;
import ru.practicum.item.comment.CommentDto;
import ru.practicum.item.NewItemRequest;
import ru.practicum.item.UpdateItemRequest;
import ru.practicum.item.comment.NewCommentRequest;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, NewItemRequest newItemRequest);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    ItemDto getItemById(Long itemId, Long requesterId);

    Item getEntityById(Long itemId);

    List<ItemDto> getAllByOwner(Long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, NewCommentRequest newCommentRequest);
}