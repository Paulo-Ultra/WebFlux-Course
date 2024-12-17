package br.com.pauloultra.webfluxcourse.controller;

import br.com.pauloultra.webfluxcourse.entity.User;
import br.com.pauloultra.webfluxcourse.mapper.UserMapper;
import br.com.pauloultra.webfluxcourse.model.request.UserRequest;
import br.com.pauloultra.webfluxcourse.model.response.UserResponse;
import br.com.pauloultra.webfluxcourse.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static reactor.core.publisher.Mono.just;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    public static final String ID = "123456";
    public static final String NAME = "Paulo";
    public static final String EMAIL = "paulo@mail.com";
    public static final String PASSWORD = "123";
    public static final String NAME_WITH_WHITE_SPACES = " Paulo";
    public static final String BASE_URI = "/users";
    public static final User USER = User.builder().build();

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper mapper;

    @Test
    @DisplayName("Test endpoint save with success")
    void testSaveWithSuccess() {
        final var request = new UserRequest(NAME, EMAIL, PASSWORD);

        when(userService.save(any(UserRequest.class))).thenReturn(just(USER));

        webTestClient.post().uri(BASE_URI)
                .contentType(APPLICATION_JSON)
                .body(fromValue(request))
                .exchange()
                .expectStatus().isCreated();

        verify(userService, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint save with bad request")
    void testSaveWithBadRequest() {
        final var request = new UserRequest(NAME_WITH_WHITE_SPACES, EMAIL, PASSWORD);

        webTestClient.post().uri(BASE_URI)
                .contentType(APPLICATION_JSON)
                .body(fromValue(request))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo(BASE_URI)
                .jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation error")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("field cannot have blank spaces at the beginning or at end");

    }

    @Test
    @DisplayName("Test endpoint find by id with success")
    void testFindByIdWithSuccess() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(userService.findById(anyString())).thenReturn(just(USER));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri(BASE_URI + "/" + ID)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(userService).findById(anyString());
        verify(mapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test endpoint find all with success")
    void testFindAllWithSuccess() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);

        when(userService.findAll()).thenReturn(Flux.just(USER));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri(BASE_URI)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(ID)
                .jsonPath("$.[0].name").isEqualTo(NAME)
                .jsonPath("$.[0].email").isEqualTo(EMAIL)
                .jsonPath("$.[0].password").isEqualTo(PASSWORD);

        verify(userService).findAll();
        verify(mapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test endpoint update with success")
    void testUpdateWithSucces() {
        final var userResponse = new UserResponse(ID, NAME, EMAIL, PASSWORD);
        final var userRequest = new UserRequest(NAME, EMAIL, PASSWORD);

        when(userService.update(anyString(), any(UserRequest.class))).thenReturn(just(USER));
        when(mapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.patch().uri(BASE_URI + "/" + ID)
                .contentType(APPLICATION_JSON)
                .body(fromValue(userRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL)
                .jsonPath("$.password").isEqualTo(PASSWORD);

        verify(userService).update(anyString(), any(UserRequest.class));
        verify(mapper).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test endpoint delete with success")
    void testDeleteWithSuccess() {

        when(userService.delete(anyString())).thenReturn(just(USER));
        webTestClient.delete().uri(BASE_URI + "/" + ID)
                .exchange()
                .expectStatus().isOk();

        verify(userService).delete(anyString());
    }
}