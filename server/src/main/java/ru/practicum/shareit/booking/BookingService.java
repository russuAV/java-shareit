package ru.practicum.shareit.booking;


import ru.practicum.booking.BookingDto;
import ru.practicum.booking.NewBookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, NewBookingRequestDto dto);

    BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByUser(Long userId, BookingState state);

    List<BookingDto> getBookingsByOwner(Long ownerId, BookingState state);
}