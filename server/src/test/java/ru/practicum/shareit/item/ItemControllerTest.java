package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.BookingStatus;
import ru.practicum.item.NewItemRequest;
import ru.practicum.item.UpdateItemRequest;
import ru.practicum.item.comment.NewCommentRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(new User(null, "test@example.com", "Test User"));
        userId = user.getId();
    }

    @Test
    void createItem() throws Exception {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName("Drill");
        newItemRequest.setDescription("Powerful drill");
        newItemRequest.setAvailable(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void updateItem() throws Exception {
        Item item = itemRepository.save(Item.builder()
                .name("Old Name")
                .description("Old Desc")
                .available(true)
                .owner(userRepository.findById(userId).get())
                .build());

        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setName("New Name");

        mvc.perform(patch("/items/" + item.getId())
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void getById() throws Exception {
        Item item = itemRepository.save(Item.builder()
                .name("Hammer")
                .description("For nails")
                .available(true)
                .owner(userRepository.findById(userId).get())
                .build());

        mvc.perform(get("/items/" + item.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hammer"));
    }

    @Test
    void getAllByOwner() throws Exception {
        itemRepository.save(Item.builder()
                .name("Item1")
                .description("Desc1")
                .available(true)
                .owner(userRepository.findById(userId).get())
                .build());

        itemRepository.save(Item.builder()
                .name("Item2")
                .description("Desc2")
                .available(true)
                .owner(userRepository.findById(userId).get())
                .build());

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void searchItems() throws Exception {
        itemRepository.save(Item.builder()
                .name("Screwdriver")
                .description("For screws")
                .available(true)
                .owner(userRepository.findById(userId).get())
                .build());

        mvc.perform(get("/items/search")
                        .param("text", "screw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", containsString("Screw")));
    }

    @Test
    void addComment() throws Exception {
        User owner = userRepository.findById(userId).get();

        User booker = userRepository.save(new User(null, "test@test.com", "Test User"));

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Power tool")
                .available(true)
                .owner(owner)
                .build());

        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build());

        NewCommentRequest commentRequest = new NewCommentRequest();
        commentRequest.setText("Great tool!");

        mvc.perform(post("/items/" + item.getId() + "/comment")
                        .header("X-Sharer-User-Id", booker.getId()) // ключевой момент!
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great tool!"));
    }
}