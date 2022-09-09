package be.bbconsulting.hellodevoxx.xray;

import com.amazonaws.xray.spring.aop.BaseAbstractXRayInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnProperty(value = "aws.xray.enabled", havingValue = "true", matchIfMissing = true)
public class XRayInterceptor extends BaseAbstractXRayInterceptor {

    @Override
    @Pointcut("@within(com.amazonaws.xray.spring.aop.XRayEnabled)")
    public void xrayEnabledClasses() {
        System.out.println("xray");
        // no logic needed here
    }
}
