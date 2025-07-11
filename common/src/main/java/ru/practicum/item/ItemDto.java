package ru.practicum.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.booking.BookingDto;
import ru.practicum.item.comment.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}