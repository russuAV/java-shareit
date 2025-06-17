package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void testEqualsAndHashCode() {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("A");
        u1.setEmail("a@a.com");

        User u2 = new User();
        u2.setId(1L);
        u2.setName("A");
        u2.setEmail("a@a.com");

        User u3 = new User();
        u3.setId(2L);
        u3.setName("B");
        u3.setEmail("b@b.com");

        assertThat(u1).isEqualTo(u2);
        assertThat(u1).hasSameHashCodeAs(u2);
        assertThat(u1).isNotEqualTo(u3);
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@test.com");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
    }
}