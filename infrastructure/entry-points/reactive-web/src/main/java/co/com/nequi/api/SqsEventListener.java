package co.com.nequi.api;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserNoSqlRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsEventListener {

    private final SqsAsyncClient sqsClient;
    private final UserNoSqlRepository userNoSqlRepository;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.sqs.queue-url}")
    private String queueUrl;

    @PostConstruct
    public void startListening() {
        pollMessages().subscribe();
    }

    private Mono<Void> pollMessages() {
        return Mono.fromFuture(() -> sqsClient.receiveMessage(
                        ReceiveMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .maxNumberOfMessages(5)
                                .waitTimeSeconds(5)
                                .build()
                ))
                .flatMapMany(response -> Flux.fromIterable(response.messages()))
                .flatMap(this::processMessage)
                .then()
                .doFinally(signal -> pollMessages().subscribe());
    }

    private Mono<Void> processMessage(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), User.class))
                .subscribeOn(Schedulers.boundedElastic())
                .map(user -> User.builder()
                        .id(user.getId())
                        .email(user.getEmail().toUpperCase())
                        .firstName(user.getFirstName().toUpperCase())
                        .lastName(user.getLastName().toUpperCase())
                        .avatar(user.getAvatar())
                        .build())
                .doOnNext(upperCaseUser -> log.info("Mensaje recibido y transformado",
                        kv("id", upperCaseUser.getId()),
                        kv("email", upperCaseUser.getEmail()),
                        kv("name", upperCaseUser.getFirstName()),
                        kv("lastName", upperCaseUser.getLastName()),
                        kv("avatar", upperCaseUser.getAvatar())))
                .flatMap(userNoSqlRepository::save)
                .then(Mono.fromFuture(() ->
                        sqsClient.deleteMessage(b -> b
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle()))))
                .then()
                .onErrorResume(e -> {
                    log.error("Error procesando mensaje: {}", e.getMessage());
                    return Mono.empty();
                });
    }
}
