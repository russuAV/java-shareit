package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.CreateItemRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper mapper;

    private User requester;
    private User owner;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        requester = userRepository.save(new User(null, "requester@mail.com", "Requester"));
        owner = userRepository.save(new User(null, "11111@mail.com", "Owner"));
    }

    @Test
    void createRequest() throws Exception {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("Need a drill");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requester.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Need a drill")));
    }

    @Test
    void getOwnRequests() throws Exception {
        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("Need a saw")
                .requester(requester)
                .created(LocalDateTime.now())
                .build());

        itemRepository.save(Item.builder()
                .name("Saw")
                .description("Wood saw")
                .available(true)
                .owner(owner)
                .requestId(request.getId())
                .build());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requester.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Need a saw")))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Saw")));
    }

    @Test
    void getAllRequests() throws Exception {
        itemRequestRepository.save(ItemRequest.builder()
                .description("Need a hammer")
                .requester(requester)
                .created(LocalDateTime.now())
                .build());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Need a hammer")));
    }

    @Test
    void getRequestById() throws Exception {
        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("Need a wrench")
                .requester(requester)
                .created(LocalDateTime.now())
                .build());

        itemRepository.save(Item.builder()
                .name("Wrench")
                .description("Metal wrench")
                .available(true)
                .owner(owner)
                .requestId(request.getId())
                .build());

        mvc.perform(get("/requests/" + request.getId())
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Need a wrench")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", is("Wrench")));
    }
}