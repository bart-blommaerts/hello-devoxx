package be.bbconsulting.hellodevoxx.web;

import be.bbconsulting.hellodevoxx.dynamodb.GreeterService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


@RestController
@XRayEnabled
public class GreetingController {

    GreeterService greeterService;
    DynamoDbClient ddb;

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
    public String home() {
        String value = greeterService.getDynamoDBItem(ddb, "greeter","greeting", "hello darkness, my old friend" );

        return value;
    }
}
