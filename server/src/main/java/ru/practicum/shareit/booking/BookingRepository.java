package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
            AND b.status IN ('APPROVED', 'WAITING')
            AND b.end > :start AND b.start < :end
            """)
    List<Booking> findOverlappingBookings(@Param("itemId") Long itemId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
            AND b.start < :now
            AND b.status = 'APPROVED'
            ORDER BY b.start DESC LIMIT 1
            """)
    Booking findLastBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.item.id = :itemId
            AND b.start > :now
            AND b.status = 'APPROVED'
            ORDER BY b.start ASC LIMIT 1
            """)
    Booking findNextBooking(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime now1, LocalDateTime now2);

    boolean existsByItemIdAndBookerIdAndEndBeforeAndStatus(
            Long itemId, Long userId, LocalDateTime now, BookingStatus status);
}