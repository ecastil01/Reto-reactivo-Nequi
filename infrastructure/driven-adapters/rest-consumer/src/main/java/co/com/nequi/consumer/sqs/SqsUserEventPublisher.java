package co.com.nequi.consumer.sqs;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsUserEventPublisher implements UserEventPublisher {

    private final SqsAsyncClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.sqs.queue-url}")
    private String queueUrl;

    @Override
    public Mono<Void> publishUserCreated(User user) {
        try {
            String message = objectMapper.writeValueAsString(user);
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(message)
                    .build();

            return Mono.fromFuture(sqsClient.sendMessage(request))
                    .doOnSuccess(resp -> log.info("Evento enviado a SQS: " + user))
                    .then();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}