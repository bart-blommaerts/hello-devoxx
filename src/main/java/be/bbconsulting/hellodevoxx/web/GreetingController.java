package be.bbconsulting.hellodevoxx.web;

import be.bbconsulting.hellodevoxx.dynamodb.GreeterService;
import org.springframework.beans.factory.ListableBeanFactoryExtensionsKt;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.springframework.http.HttpStatus.ACCEPTED;


@RestController
@XRayEnabled
public class GreetingController {

    GreeterService greeterService;
    DynamoDbClient ddb;

    private static final String GREETING_TABLE_NAME = "greeter";
    private static final String GREETING_KEY = "greeting";

    public GreetingController(GreeterService greeterService) {
        this.greeterService = greeterService;

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.EU_WEST_3;
        ddb = DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    @RequestMapping("/")
    public String retrieve() {
        String value = greeterService.getDynamoDBItem(ddb, GREETING_TABLE_NAME,GREETING_KEY, "hello darkness, my old friend" );
        return value;
    }

    @PostMapping("/")
    @ResponseStatus(ACCEPTED)
     public void create(@RequestBody String greeting) {
        greeterService.putItemInTable(ddb, GREETING_TABLE_NAME, GREETING_KEY, greeting);
    }
}
