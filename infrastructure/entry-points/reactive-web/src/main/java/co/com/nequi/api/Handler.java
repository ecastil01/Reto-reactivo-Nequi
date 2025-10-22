package co.com.nequi.api;

import co.com.nequi.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase useCase;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        return useCase.createUser(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        return useCase.getUserById(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(error -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(error.getMessage()));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return useCase.getAllUsers()
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(users));
    }

    public Mono<ServerResponse> getUsersByName(ServerRequest serverRequest) {
        String name = serverRequest.queryParam("name").orElse("");
        return useCase.getUsersByName(name)
                .collectList()
                .flatMap(users -> ServerResponse.ok().bodyValue(users));
    }
}
