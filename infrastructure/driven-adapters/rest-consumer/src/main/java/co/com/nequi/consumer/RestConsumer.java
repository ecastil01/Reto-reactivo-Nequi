package co.com.nequi.consumer;

import co.com.nequi.model.enums.TechnicalMessage;
import co.com.nequi.model.exception.BusinessException;
import co.com.nequi.model.exception.TechnicalException;
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
                .onStatus(status -> status.value() == 404, response ->
                        Mono.error(new BusinessException(TechnicalMessage.USER_NOT_FOUND))
                )
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("Client error")
                                .flatMap(body -> Mono.error(new TechnicalException(
                                        String.format("Client error (%d): %s", response.statusCode().value(), body),
                                        TechnicalMessage.USER_NOT_FOUND)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("No error body")
                                .flatMap(body -> Mono.error(new TechnicalException(
                                        String.format("Server error (%d) while fetching user %d: %s",
                                                response.statusCode().value(), id, body),
                                        TechnicalMessage.USER_NOT_FOUND)))
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
