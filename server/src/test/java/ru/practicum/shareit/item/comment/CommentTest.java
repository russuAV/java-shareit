package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void testEqualsSameId() {
        Comment comment1 = Comment.builder().id(1L).build();
        Comment comment2 = Comment.builder().id(1L).build();
        Comment comment3 = Comment.builder().id(2L).build();

        assertEquals(comment1, comment2, "Objects with same id must be equal");
        assertNotEquals(comment1, comment3, "Objects with different id must not be equal");
        assertNotEquals(comment1, null, "Equals must return false for null");
        assertNotEquals(comment1, new Object(), "Equals must return false for other type");
    }

    @Test
    void testHashCodeAlwaysSame() {
        Comment comment1 = Comment.builder().id(1L).build();
        Comment comment2 = Comment.builder().id(2L).build();

        assertEquals(comment1.hashCode(), comment2.hashCode(),
                "Hash codes should be equal because implementation always returns constant");
    }
}