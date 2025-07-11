package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.xml.sax.ErrorHandler;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({BookingController.class, ErrorHandler.class})
class ErrorHandlerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @Test
    void whenServiceThrowsNotFound_thenControllerReturns404() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("no such"));

        mvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("no such"));
    }
}