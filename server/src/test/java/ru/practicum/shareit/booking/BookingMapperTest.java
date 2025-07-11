package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.booking.BookingDto;
import ru.practicum.booking.NewBookingRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toAndFromDto() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end   = start.plusDays(1);

        NewBookingRequestDto req = new NewBookingRequestDto();
        req.setItemId(3L);
        req.setStart(start);
        req.setEnd(end);

        User booker = new User(2L, "u@u.com", "U");
        Item item   = Item.builder()
                .id(3L)
                .name("drill")
                .description("tool")
                .available(true)
                .owner(booker)
                .build();

        Booking domain = BookingMapper.toBooking(req, item, booker);
        assertThat(domain.getItem()).isEqualTo(item);
        assertThat(domain.getBooker()).isEqualTo(booker);
        assertThat(domain.getStart()).isEqualTo(start);
        assertThat(domain.getEnd()).isEqualTo(end);

        BookingDto dto = BookingMapper.toDto(domain);
        assertThat(dto.getItem().getId()).isEqualTo(item.getId());
        assertThat(dto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }
}
