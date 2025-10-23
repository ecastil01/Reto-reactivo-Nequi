package co.com.nequi.r2dbc.DynamoDb;

import co.com.nequi.model.user.User;
import co.com.nequi.model.user.gateways.UserNoSqlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Component
@RequiredArgsConstructor
public class DynamoUserRepository implements UserNoSqlRepository {

    private final DynamoDbEnhancedAsyncClient dynamoClient;

    @Override
    public Mono<User> save(User user) {
        UserDynamoEntity entity = UserDynamoEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .build();
        
        DynamoDbAsyncTable<UserDynamoEntity> table = dynamoClient.table("UsersUpperCase", TableSchema.fromBean(UserDynamoEntity.class));
        return Mono.fromFuture(table.putItem(entity))
                .thenReturn(user);
    }
}