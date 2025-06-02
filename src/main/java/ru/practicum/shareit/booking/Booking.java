package ru.practicum.shareit.booking;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Booking {

    public enum Status {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;
}