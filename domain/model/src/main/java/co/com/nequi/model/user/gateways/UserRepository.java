package co.com.nequi.model.user.gateways;

import co.com.nequi.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> saveUser(User user);
    Mono<User> getUserById(Long id);
    Flux<User> getAllUsers();
    Flux<User> getUserByName(String name);
}
