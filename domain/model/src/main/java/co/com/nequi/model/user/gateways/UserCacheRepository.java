package co.com.nequi.model.user.gateways;

import co.com.nequi.model.user.User;
import reactor.core.publisher.Mono;

public interface UserCacheRepository {
    Mono<User> findById(String id);
    Mono<User> save(String id, User user);
}