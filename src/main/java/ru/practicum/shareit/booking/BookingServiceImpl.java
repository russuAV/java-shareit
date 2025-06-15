package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BookingServiceImpl implements  BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking createBooking(Long userId, BookingRequestDto dto) {
        User booker = userService.getUserById(userId);
        Item item = itemService.getItemById(dto.getItemId(), userId);

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }

        List<Booking> conflicts = bookingRepository.findOverlappingBookings(item.getId(), dto.getStart(), dto.getEnd());
        if (!conflicts.isEmpty()) {
            throw new ValidationException("Вещь уже забронирована на указанный период.");
        }

        Booking booking = BookingMapper.toBooking(dto, item, booker);
        booking.setStatus(Booking.BookingStatus.WAITING);

        log.info("Создано бронирование на вещь с ID {}", item.getId());
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new ForbiddenException("Только владелец может подтверждать бронирование");
        }
        if (booking.getStatus() != Booking.BookingStatus.WAITING) {
            throw new ConflictException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? Booking.BookingStatus.APPROVED : Booking.BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!Objects.equals(userId, bookerId) && !Objects.equals(userId, ownerId)) {
            throw new NotFoundException("Нет доступа к бронированию");
        }

        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUser(Long userId, BookingState state) {
        userService.getUserById(userId);
        return findByState(userId, state, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByOwner(Long ownerId, BookingState state) {
        userService.getUserById(ownerId);
        return findByState(ownerId, state, false);
    }

    private List<Booking> findByState(Long id, BookingState state, boolean byBooker) {
        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case ALL -> byBooker
                    ? bookingRepository.findByBookerIdOrderByStartDesc(id)
                    : bookingRepository.findByItemOwnerIdOrderByStartDesc(id);
            case CURRENT -> byBooker
                    ? bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(id, now, now)
                    : bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(id, now, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(id, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(id, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(id,
                    Booking.BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(id,
                    Booking.BookingStatus.REJECTED);
        };
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }
}
