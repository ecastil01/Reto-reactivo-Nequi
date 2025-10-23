package co.com.nequi.model.user.gateways;


import co.com.nequi.model.user.User;
import reactor.core.publisher.Mono;

public interface UserNoSqlRepository {
    Mono<User> save(User user);
}