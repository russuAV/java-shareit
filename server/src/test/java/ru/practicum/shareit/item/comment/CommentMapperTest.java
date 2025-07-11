package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.item.comment.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toDto_shouldMapFieldsCorrectly() {
        User author = User.builder().id(1L).name("John").build();
        Item item = Item.builder().id(2L).build();
        LocalDateTime created = LocalDateTime.now();

        Comment comment = Comment.builder()
                .id(3L)
                .message("Good item!")
                .author(author)
                .item(item)
                .created(created)
                .build();

        CommentDto dto = CommentMapper.toDto(comment);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getText()).isEqualTo("Good item!");
        assertThat(dto.getAuthorName()).isEqualTo("John");
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void toComment_shouldMapFieldsCorrectly() {
        CommentDto dto = CommentDto.builder()
                .text("Nice one")
                .build();

        User author = User.builder().id(1L).name("Alice").build();
        Item item = Item.builder().id(2L).build();

        Comment comment = CommentMapper.toComment(dto, item, author);

        assertThat(comment.getMessage()).isEqualTo("Nice one");
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getCreated()).isNotNull();
    }
}