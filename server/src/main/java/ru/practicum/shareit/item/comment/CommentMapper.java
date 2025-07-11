package ru.practicum.shareit.item.comment;


import ru.practicum.item.comment.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getMessage())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto dto, Item item, User author) {
        return Comment.builder()
                .message(dto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }
}