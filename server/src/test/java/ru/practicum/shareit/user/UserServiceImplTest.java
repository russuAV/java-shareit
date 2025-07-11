package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UpdateUserRequest;
import ru.practicum.user.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class UserServiceImplTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createAndGetAllAndGetById() {
        UserDto u1 = userService.create(NewUserRequest.builder()
                .email("a@a.com")
                .name("A")
                .build());
        UserDto u2 = userService.create(NewUserRequest.builder()
                .email("b@b.com")
                .name("B")
                .build());

        List<UserDto> all = userService.getAll();
        assertThat(all).extracting(UserDto::getEmail).containsExactlyInAnyOrder("a@a.com","b@b.com");

        UserDto found = userService.getUserById(u1.getId());
        assertThat(found.getEmail()).isEqualTo("a@a.com");
    }

    @Test
    void updateAndDelete() {
        UserDto u = userService.create(NewUserRequest.builder()
                .email("x@x.com")
                .name("X")
                .build());
        Long id = u.getId();

        UserDto updated = userService.update(UpdateUserRequest.builder()
                .id(id)
                .email("u@u.com")
                .name("U")
                .build());
        assertThat(updated.getEmail()).isEqualTo("u@u.com");
        userService.delete(id);
        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateThrowsWhenUserNotFound() {
        assertThatThrownBy(() -> userService.update(
                UpdateUserRequest.builder().id(999L).email("x@x.com").build()
        )).isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteThrowsWhenUserNotFound() {
        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getEntityByIdReturnsCorrectUser() {
        UserDto u = userService.create(NewUserRequest.builder()
                .email("z@z.com").name("Z").build());
        User entity = userService.getEntityById(u.getId());
        assertThat(entity.getEmail()).isEqualTo("z@z.com");
    }
}