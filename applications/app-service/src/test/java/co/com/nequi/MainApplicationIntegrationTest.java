package co.com.nequi;

import co.com.nequi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
    "spring.r2dbc.url=r2dbc:h2:mem:///testdb",
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "cloud.aws.sqs.queue-url=http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/user-created",
})
class MainApplicationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldGetAllUsersSuccessfully() {
        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class);
    }

    @Test
    void shouldReturnNotFoundForNonExistentUser() {
        webTestClient.get()
                .uri("/api/v1/user/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldSearchUsersByName() {
        webTestClient.get()
                .uri("/api/v1/users/name?name=test")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class);
    }
}