package co.com.nequi.r2dbc;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserRepository;
import co.com.nequi.r2dbc.entity.UserEntity;
import co.com.nequi.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        Long,
        MyReactiveRepository
        > implements UserRepository {
    private final TransactionalOperator transactionalOperator;
    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper,
                                       TransactionalOperator transactionalOperator) {

        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> saveUser(User user) {
        UserEntity entity = toData(user);
        entity.setNew(true);
        return repository.save(entity)
                .map(this::toEntity)
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<User> getUserById(Long id) {
        return findById(id);
    }

    @Override
    public Flux<User> getAllUsers() {
        return findAll();
    }

    public Flux<User> getUserByName(String name) {
        return repository.findByFirstNameContainingIgnoreCase(name)
                .map(this::toEntity);
    }
}
