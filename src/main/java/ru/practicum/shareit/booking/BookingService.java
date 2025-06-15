package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, BookingRequestDto dto);

    Booking approveBooking(Long ownerId, Long bookingId, boolean approved);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getBookingsByUser(Long userId, BookingState state);

    List<Booking> getBookingsByOwner(Long ownerId, BookingState state);
}