package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.booking.BookingDto;
import ru.practicum.booking.BookingStatus;
import ru.practicum.item.ItemDto;
import ru.practicum.user.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoJsonTest {

    private final JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        ItemDto item = ItemDto.builder()
                .id(2L)
                .name("bar")
                .build();

        UserDto booker = UserDto.builder()
                .id(5L)
                .name("A")
                .email("a@a.com")
                .build();

        BookingDto dto = BookingDto.builder()
                .id(10L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.of(2025, 6, 16, 12, 0))
                .end(LocalDateTime.of(2025, 6, 17, 12, 0))
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("bar");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("A");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("a@a.com");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-06-16T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-06-17T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("APPROVED");
    }
}