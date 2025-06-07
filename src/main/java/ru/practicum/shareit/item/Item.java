package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "request_id")
    private Long requestId;

    @Transient
    private Booking lastBooking;

    @Transient
    private Booking nextBooking;

    @Transient
    private List<Comment> comments;
}