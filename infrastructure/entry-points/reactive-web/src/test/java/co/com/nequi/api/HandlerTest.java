package co.com.nequi.api;

import co.com.nequi.model.user.User;
import co.com.nequi.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private Handler handler;

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .build();

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .build();

        when(userUseCase.createUser(anyLong())).thenReturn(Mono.just(user));

        Mono<ServerResponse> response = handler.createUser(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode().value() == 200)
                .verifyComplete();
    }
}