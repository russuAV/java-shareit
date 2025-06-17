package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingDto;
import ru.practicum.booking.NewBookingRequestDto;
import ru.practicum.booking.BookingStatus;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
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
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public BookingDto createBooking(Long userId, NewBookingRequestDto dto) {
        User booker = userService.getEntityById(userId);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + dto.getItemId() + " не найдена."));


        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }

        List<Booking> conflicts = bookingRepository.findOverlappingBookings(item.getId(), dto.getStart(), dto.getEnd());
        if (!conflicts.isEmpty()) {
            throw new ValidationException("Вещь уже забронирована на указанный период.");
        }

        Booking booking = BookingMapper.toBooking(dto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        log.info("Создано бронирование на вещь с ID {}", item.getId());
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new ForbiddenException("Только владелец может подтверждать бронирование");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ConflictException("Бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!Objects.equals(userId, bookerId) && !Objects.equals(userId, ownerId)) {
            throw new NotFoundException("Нет доступа к бронированию");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUser(Long userId, BookingState state) {
        userService.getUserById(userId);

        return findByState(userId, state, true).stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwner(Long ownerId, BookingState state) {
        userService.getUserById(ownerId);

        return findByState(ownerId, state, false).stream()
                .map(BookingMapper::toDto)
                .toList();
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
                    BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(id,
                    BookingStatus.REJECTED);
        };
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }
}
