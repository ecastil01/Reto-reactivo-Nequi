package co.com.nequi.model.user.consumer;

import co.com.nequi.model.user.User;
import reactor.core.publisher.Mono;

public interface UserConsumerGateway {
    Mono<User> getUserById(Long id);
}
