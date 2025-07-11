package ru.practicum.shareit.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void handleNotFound() throws Exception {
        mvc.perform(get("/users/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message", containsString("не найден")))
                .andExpect(jsonPath("$.path").value("/users/99999"));
    }

    @Test
    void handleValidation() throws Exception {
        // Некорректный email => валидация должна сработать
        mvc.perform(
                        post("/users")
                                .contentType("application/json")
                                .content("{\"name\": \"test\", \"email\": \"not-an-email\"}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("email: Некорректный email")))
                .andExpect(jsonPath("$.path").value("/users"));
    }

    @Test
    void handleConflict() throws Exception {
        String userJson = "{\"name\": \"Test\", \"email\": \"test@test.com\"}";
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message", containsString("Данный email уже используется")))
                .andExpect(jsonPath("$.path").value("/users"));
    }

    @Test
    void handleForbidden() throws Exception {
        User owner = userRepository.save(new User(null, "owner1@mail.com", "Owner"));
        User other = userRepository.save(new User(null, "other@mail.com", "Other"));

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Power tool")
                .available(true)
                .owner(owner)
                .build()
        );

        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(other)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build()
        );

        mvc.perform(patch("/bookings/" + booking.getId() + "?approved=true")
                        .header("X-Sharer-User-Id", other.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void handleInternalServerError() throws Exception {
        mvc.perform(get("/exception-test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message", containsString("Произошла ошибка")))
                .andExpect(jsonPath("$.path").value("/exception-test"));
    }
}