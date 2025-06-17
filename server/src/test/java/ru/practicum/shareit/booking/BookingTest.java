package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    void testEqualsAndHashCode() {
        User u = new User();
        Item i = new Item();
        LocalDateTime now = LocalDateTime.now();

        Booking b1 = Booking.builder()
                .id(1L)
                .item(i)
                .booker(u)
                .start(now)
                .end(now.plusHours(1))
                .build();

        Booking b2 = Booking.builder()
                .id(1L)
                .item(i)
                .booker(u)
                .start(now)
                .end(now.plusHours(1))
                .build();

        Booking b3 = Booking.builder()
                .id(2L)
                .build();

        assertThat(b1).isEqualTo(b2);
        assertThat(b1).hasSameHashCodeAs(b2);
        assertThat(b1).isNotEqualTo(b3);
    }

    @Test
    void testGettersAndSetters() {
        Booking b = new Booking();
        b.setId(1L);
        assertThat(b.getId()).isEqualTo(1L);
    }
}