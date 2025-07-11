package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndLoadComment() {
        User author = userRepository.save(new User(null, "author@mail.com", "Author"));
        User owner = userRepository.save(new User(null, "owner@mail.com", "Owner"));

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Tool")
                .available(true)
                .owner(owner)
                .build());

        Comment comment = Comment.builder()
                .message("Nice tool!")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);

        Comment loaded = commentRepository.findById(saved.getId()).orElseThrow();

        assertThat(loaded.getMessage()).isEqualTo("Nice tool!");
        assertThat(loaded.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(loaded.getItem().getId()).isEqualTo(item.getId());
    }
}