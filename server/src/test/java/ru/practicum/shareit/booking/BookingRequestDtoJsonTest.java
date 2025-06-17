package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.booking.NewBookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<NewBookingRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        NewBookingRequestDto dto = new NewBookingRequestDto();
        dto.setItemId(5L);
        dto.setStart(LocalDateTime.of(2025, 6, 16, 8, 30));
        dto.setEnd(LocalDateTime.of(2025, 6, 17, 9, 45));

        JsonContent<NewBookingRequestDto> content = json.write(dto);

        assertThat(content).hasJsonPathNumberValue("$.itemId");
        assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(content).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-06-16T08:30:00");
        assertThat(content).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-06-17T09:45:00");
    }

    @Test
    void testRoundTripSerializeDeserialize() throws Exception {
        NewBookingRequestDto original = new NewBookingRequestDto();
        original.setItemId(5L);
        original.setStart(LocalDateTime.of(2025, 6, 16, 8, 30));
        original.setEnd(LocalDateTime.of(2025, 6, 17, 9, 45));

        String jsonString = json.write(original).getJson();

        NewBookingRequestDto parsed = json.parseObject(jsonString);

        assertThat(parsed.getItemId()).isEqualTo(original.getItemId());
        assertThat(parsed.getStart()).isEqualTo(original.getStart());
        assertThat(parsed.getEnd()).isEqualTo(original.getEnd());
    }
}