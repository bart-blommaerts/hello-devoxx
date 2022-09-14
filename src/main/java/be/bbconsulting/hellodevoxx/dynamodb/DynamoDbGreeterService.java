package be.bbconsulting.hellodevoxx.dynamodb;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DynamoDbGreeterService {

    public static String getDynamoDBItem(DynamoDbClient ddb,String tableName,String key,String keyVal) {

        HashMap<String,AttributeValue> keyToGet = new HashMap();

        keyToGet.put(key, AttributeValue.builder()
                .s(keyVal).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String,AttributeValue> returnedItem = ddb.getItem(request).item();

            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");

                for (String key1 : keys) {
                    String value = returnedItem.get(key1).s();
                    System.out.format("%s: %s\n", key1, value);
                    return value;
                }
            } else {
                System.out.format("No item found with the key %s!\n", key);
                return "";
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static void putItemInTable(DynamoDbClient ddb,
                                      String tableName,
                                      String key,
                                      String keyVal){

        HashMap<String,AttributeValue> itemValues = new HashMap();

        // Add all content to the table
        itemValues.put(key, AttributeValue.builder().s(keyVal).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            ddb.putItem(request);

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
