package be.bbconsulting.hellodevoxx.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EKSPlugin;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;
import com.amazonaws.xray.strategy.sampling.SamplingStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.net.URL;

@Configuration
@ConditionalOnProperty(value = "aws.xray.enabled", havingValue = "true", matchIfMissing = true)
public class XRayConfig {

    @Bean
    public Filter tracingFilter() {
        return new AWSXRayServletFilter("hello-devoxx");
    }

    static {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new EKSPlugin());
        URL ruleFile = XRayConfig.class.getClassLoader().getResource("sampling-rules.json");
        SamplingStrategy samplingStrategy = new LocalizedSamplingStrategy(ruleFile);
        builder.withSamplingStrategy(samplingStrategy);
        AWSXRay.setGlobalRecorder(builder.build());
    }
}