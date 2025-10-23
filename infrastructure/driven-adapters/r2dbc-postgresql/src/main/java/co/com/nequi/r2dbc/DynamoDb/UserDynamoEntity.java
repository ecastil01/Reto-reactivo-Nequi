package co.com.nequi.r2dbc.DynamoDb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class UserDynamoEntity {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;

    @DynamoDbPartitionKey
    public Long getId() {
        return id;
    }
}