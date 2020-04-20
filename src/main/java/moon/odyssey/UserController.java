package moon.odyssey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping(path = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @Value("${my.test.encValue:}")
    private String testValue;

    @GetMapping
    public Flux<User> getAllUsers() {

        return
            Flux.defer(() -> Flux.fromIterable(userRepository.findAll()))
                .subscribeOn(Schedulers.elastic())
            ;
    }

    @GetMapping(path = "/{userId}")
    public Mono<User> getUser(@PathVariable String userId) {
        return
            Mono.defer(() -> userRepository.findByUserId(userId).map(Mono::just).orElseGet(Mono::empty))
                .subscribeOn(Schedulers.elastic())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not Found user for " + userId)))
            ;
    }

    @GetMapping(path = "/encValue")
    public Mono<String> getEncValue() {
        return
            Mono.just(testValue);
    }

}
