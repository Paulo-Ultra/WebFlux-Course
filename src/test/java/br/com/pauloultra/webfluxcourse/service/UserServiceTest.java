package br.com.pauloultra.webfluxcourse.service;

import br.com.pauloultra.webfluxcourse.entity.User;
import br.com.pauloultra.webfluxcourse.mapper.UserMapper;
import br.com.pauloultra.webfluxcourse.model.request.UserRequest;
import br.com.pauloultra.webfluxcourse.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final User USER = User.builder().build();

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testSave() {
        UserRequest request = new UserRequest("Paulo", "paulo@mail.com","123");
        User entity = User.builder().build();

        when(mapper.toEntity(any())).thenReturn(entity);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(USER));

        Mono<User> result = userService.save(request);

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        Mockito.verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindById() {
        when(userRepository.findById(anyString())).thenReturn(Mono.just(User.builder()
                    .id("1234")
                .build()));

        Mono<User> result = userService.findById("123");

        StepVerifier.create(result)
//                .expectNextMatches(user -> user.getClass() == User.class && user.getId() == "1234")
                .expectNextMatches(user -> user.getClass() == User.class && Objects.equals(user.getId(), "1234"))
                .expectComplete()
                .verify();

        Mockito.verify(userRepository, times(1)).findById(anyString());
    }

    @Test
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(Flux.just(USER));

        Flux<User> result = userService.findAll();

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class)
                .expectComplete()
                .verify();

        Mockito.verify(userRepository, times(1)).findAll();
    }
}