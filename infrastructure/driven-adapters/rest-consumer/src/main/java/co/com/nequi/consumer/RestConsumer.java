package co.com.nequi.consumer;

import co.com.nequi.consumer.exception.ErrorFetching;
import co.com.nequi.model.user.User;
import co.com.nequi.model.user.consumer.UserConsumerGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserConsumerGateway {

    private final WebClient client;

    @CircuitBreaker(name = "UserGet")
    @Override
    public Mono<User> getUserById(Long id) {
        return client
                .get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("No error body")
                                .flatMap(body -> Mono.error(new ErrorFetching(
                                        String.format("Client error (%d) while fetching user %d: %s",
                                                response.statusCode().value(),
                                                id,
                                                body)
                                )))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("No error body")
                                .flatMap(body -> Mono.error(new ErrorFetching(
                                        String.format("Server error (%d) while fetching user %d: %s",
                                                response.statusCode().value(),
                                                id,
                                                body)
                                )))
                )
                .bodyToMono(UserConsumerResponse.class)
                .map(response -> {
                    UserConsumerResponse.UserData data = response.getData();
                    return User.builder()
                            .id(data.getId())
                            .email(data.getEmail())
                            .firstName(data.getFirstName())
                            .lastName(data.getLastName())
                            .avatar(data.getAvatar())
                            .build();
                });
    }
}
