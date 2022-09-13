package be.bbconsulting.hellodevoxx.web;

import be.bbconsulting.hellodevoxx.dynamodb.DynamoDbGreeterService;
import be.bbconsulting.hellodevoxx.sns.SnsGreetingService;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sns.SnsClient;

import static org.springframework.http.HttpStatus.ACCEPTED;


@RestController
@XRayEnabled
public class GreetingController {

    DynamoDbGreeterService dynamoDbGreeterService;
    DynamoDbClient ddb;
    SnsClient snsClient;

    SnsGreetingService snsGreetingService;

    private static final String GREETING_TABLE_NAME = "greeter";
    private static final String GREETING_KEY = "greeting";
    private static final Region region = Region.EU_WEST_3;

    ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();

    public GreetingController(DynamoDbGreeterService greeterService, SnsGreetingService snsGreetingService) {
        this.dynamoDbGreeterService = greeterService;
        this.snsGreetingService = snsGreetingService;

        ddb = DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    @RequestMapping("/")
    public String retrieve() {
        return dynamoDbGreeterService.getDynamoDBItem(ddb, GREETING_TABLE_NAME,GREETING_KEY, "hello darkness, my old friend" );
    }

    @PostMapping("/")
    @ResponseStatus(ACCEPTED)
     public void create(@RequestBody String greeting) {
        dynamoDbGreeterService.putItemInTable(ddb, GREETING_TABLE_NAME, GREETING_KEY, greeting);

        snsClient = SnsClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
        snsGreetingService.publish(snsClient, greeting, "arn:aws:sns:eu-west-3:836964591189:dynamodb");
        snsClient.close();
    }
}
