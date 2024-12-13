package br.com.pauloultra.webfluxcourse.service;

import br.com.pauloultra.webfluxcourse.entity.User;
import br.com.pauloultra.webfluxcourse.mapper.UserMapper;
import br.com.pauloultra.webfluxcourse.model.request.UserRequest;
import br.com.pauloultra.webfluxcourse.repository.UserRepository;
import br.com.pauloultra.webfluxcourse.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public Mono<User> save(final UserRequest request) {
        return userRepository.save(mapper.toEntity(request));
    }

    public Mono<User> findById(final String id){
        return handleNotFound(userRepository.findById(id), id);
    }

    public Flux<User> findAll(){
        return userRepository.findAll();
    }

    public Mono<User> update(final String id, final UserRequest request) {
        return userRepository.findById(id)
                .map(entity -> mapper.toEntity(request, entity))
                .flatMap(userRepository::save);
    }

    public Mono<User> delete(final String id) {
        return handleNotFound(userRepository.findAndRemove(id), id);
    }

    private <T> Mono<T> handleNotFound(final Mono<T> mono, final String id) {
        return mono.switchIfEmpty(
                Mono.error(
                        new ObjectNotFoundException(
                                format("Object not found. Id: %s, Type: %s", id, User.class.getSimpleName())
                        )
                ));
    }
}
