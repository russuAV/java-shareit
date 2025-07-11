package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testEqualsSameId() {
        Item item1 = Item.builder().id(1L).build();
        Item item2 = Item.builder().id(1L).build();
        Item item3 = Item.builder().id(2L).build();

        assertEquals(item1, item2, "Objects with same id must be equal");
        assertNotEquals(item1, item3, "Objects with different id must not be equal");
        assertNotEquals(item1, null, "Equals must return false for null");
        assertNotEquals(item1, new Object(), "Equals must return false for other type");
    }

    @Test
    void testHashCodeSameId() {
        Item item1 = Item.builder().id(1L).build();
        Item item2 = Item.builder().id(1L).build();

        assertEquals(item1.hashCode(), item2.hashCode(),
                "Hash codes must be equal for same id");
    }
}