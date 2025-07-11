package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.booking.BookingDto;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.NewBookingRequestDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private NewBookingRequestDto bookingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setId(1L);

        booker = new User();
        booker.setId(2L);

        item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);

        bookingRequest = new NewBookingRequestDto();
        bookingRequest.setItemId(item.getId());
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(bookingRequest.getStart());
        booking.setEnd(bookingRequest.getEnd());
    }

    @Test
    void createBooking_shouldCreateBookingSuccessfully() {
        when(userService.getEntityById(anyLong())).thenReturn(booker);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any())).thenReturn(List.of());
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.createBooking(booker.getId(), bookingRequest);

        assertNotNull(result);
        verify(bookingRepository).save(bookingCaptor.capture());
        assertEquals(BookingStatus.WAITING, bookingCaptor.getValue().getStatus());
    }

    @Test
    void createBooking_shouldThrowIfItemUnavailable() {
        item.setAvailable(false);
        when(userService.getEntityById(anyLong())).thenReturn(booker);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(booker.getId(), bookingRequest));
    }

    @Test
    void createBooking_shouldThrowIfOverlapping() {
        when(userService.getEntityById(anyLong())).thenReturn(booker);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(), any()))
                .thenReturn(List.of(new Booking()));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(booker.getId(), bookingRequest));
    }

    @Test
    void approveBooking_shouldApproveSuccessfully() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setItem(item);

        BookingDto result = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approveBooking_shouldThrowForbiddenException() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.approveBooking(
                99L, booking.getId(), true));
    }

    @Test
    void approveBooking_shouldThrowConflictException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class, () -> bookingService.approveBooking(
                owner.getId(), booking.getId(), true));
    }

    @Test
    void getBookingById_shouldReturnBookingIfOwnerOrBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setItem(item);

        assertNotNull(bookingService.getBookingById(owner.getId(), booking.getId()));
        assertNotNull(bookingService.getBookingById(booker.getId(), booking.getId()));
    }

    @Test
    void getBookingById_shouldThrowNotFoundIfUnauthorized() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(99L, booking.getId()));
    }
}