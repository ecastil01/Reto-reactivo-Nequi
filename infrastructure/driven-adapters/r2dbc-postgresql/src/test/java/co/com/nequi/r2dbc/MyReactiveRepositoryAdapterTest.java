package co.com.nequi.r2dbc;

import co.com.nequi.model.user.User;
import co.com.nequi.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @Mock
    private MyReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private MyReactiveRepositoryAdapter adapter;

    @Test
    void shouldSaveUserSuccessfully() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .avatar("http://example.com/avatar.png")
                .build();

        UserEntity entity = UserEntity.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .avatar("http://example.com/avatar.png")
                .build();

        when(mapper.map(any(User.class), any(Class.class))).thenReturn(entity);
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(UserEntity.class), any(Class.class))).thenReturn(user);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<User> result = adapter.saveUser(user);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
}