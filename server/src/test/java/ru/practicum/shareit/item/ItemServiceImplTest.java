package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.booking.BookingStatus;
import ru.practicum.item.ItemDto;
import ru.practicum.item.NewItemRequest;
import ru.practicum.item.UpdateItemRequest;
import ru.practicum.item.comment.CommentDto;
import ru.practicum.item.comment.NewCommentRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner = new User();
        owner.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(true);
        item.setName("Item1");
        item.setDescription("Description1");
    }

    @Test
    void create_shouldCreateItemWithRequestId() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Name");
        request.setDescription("Desc");
        request.setAvailable(true);
        request.setRequestId(42L);

        when(userService.getEntityById(anyLong())).thenReturn(owner);
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.create(owner.getId(), request);

        assertNotNull(result);
        verify(itemRepository).save(any());
    }

    @Test
    void create_shouldThrowIfRequestIdNotFound() {
        NewItemRequest request = new NewItemRequest();
        request.setRequestId(42L);

        when(userService.getEntityById(anyLong())).thenReturn(owner);
        when(itemRequestRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.create(owner.getId(), request));
    }

    @Test
    void update_shouldUpdateItemFields() {
        UpdateItemRequest update = new UpdateItemRequest();
        update.setName("New Name");

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemService.update(owner.getId(), item.getId(), update);

        assertNotNull(result);
        verify(itemRepository).save(item);
    }

    @Test
    void update_shouldThrowIfNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.update(999L, item.getId(), new UpdateItemRequest()));
    }

    @Test
    void getItemById_shouldIncludeBookingsIfOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(List.of());

        ItemDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result);
        verify(bookingRepository).findLastBooking(anyLong(), any());
        verify(bookingRepository).findNextBooking(anyLong(), any());
    }

    @Test
    void getItemById_shouldNotIncludeBookingsIfNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(anyLong())).thenReturn(List.of());

        ItemDto result = itemService.getItemById(item.getId(), 999L);

        assertNotNull(result);
        verify(bookingRepository, never()).findLastBooking(anyLong(), any());
    }

    @Test
    void getEntityById_shouldReturnItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertNotNull(itemService.getEntityById(item.getId()));
    }

    @Test
    void getEntityById_shouldThrowIfNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getEntityById(item.getId()));
    }

    @Test
    void getAllByOwner_shouldCallRepo() {
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getAllByOwner(owner.getId());

        assertEquals(1, result.size());
    }

    @Test
    void search_shouldReturnEmptyListOnBlank() {
        List<ItemDto> result = itemService.search("  ");

        assertTrue(result.isEmpty());
    }

    @Test
    void search_shouldReturnItems() {
        when(itemRepository.search(anyString())).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("test");

        assertEquals(1, result.size());
    }

    @Test
    void addComment_shouldAddCommentIfApprovedBookingExists() {
        User author = new User();
        author.setId(2L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setMessage("Nice!");
        comment.setCreated(LocalDateTime.now());

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.getEntityById(anyLong())).thenReturn(author);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(
                anyLong(), anyLong(), any(), eq(BookingStatus.APPROVED))
        ).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(author.getId(), item.getId(), new ru.practicum.item.comment.NewCommentRequest("Nice!"));

        assertNotNull(result);
        assertEquals("Nice!", result.getText());

        verify(userService).getEntityById(author.getId());
        verify(commentRepository).save(any(Comment.class));
    }


    @Test
    void addComment_shouldThrowIfNoApprovedBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.getEntityById(anyLong())).thenReturn(owner);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBeforeAndStatus(
                anyLong(), anyLong(), any(), eq(BookingStatus.APPROVED)))
                .thenReturn(false);

        assertThrows(ValidationException.class, () ->
                itemService.addComment(owner.getId(), item.getId(), new NewCommentRequest("test")));
    }
}