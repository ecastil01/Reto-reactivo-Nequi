package co.com.nequi.usecase.user;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.consumer.UserConsumerGateway;
import co.com.nequi.model.user.gateways.UserCacheRepository;
import co.com.nequi.model.user.gateways.UserEventPublisher;
import co.com.nequi.model.user.gateways.UserRepository;
import co.com.nequi.usecase.user.exception.NotFound;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final UserConsumerGateway userConsumerGateway;
    private final UserCacheRepository userCacheRepository;
    private final UserEventPublisher userEventPublisher;

    public Mono<User> createUser(Long id) {
        return userRepository.getUserById(id)
                .switchIfEmpty(userConsumerGateway.getUserById(id)
                        .flatMap(userRepository::saveUser)
                        .flatMap(user -> userCacheRepository.save("user:" + user.getId(), user))
                        .flatMap(user -> userEventPublisher.publishUserCreated(user).thenReturn(user)));
    }

    public Mono<User> getUserById(Long id) {
        String cacheKey = "user:" + id;
        return userCacheRepository.findById(cacheKey)
                .switchIfEmpty(userRepository.getUserById(id)
                        .flatMap(user -> userCacheRepository.save(cacheKey, user))
                        .switchIfEmpty(Mono.error(new NotFound("User not found"))));
    }

    public Flux<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public Flux<User> getUsersByName(String name) {
        return userRepository.getUserByName(name);
    }
}
