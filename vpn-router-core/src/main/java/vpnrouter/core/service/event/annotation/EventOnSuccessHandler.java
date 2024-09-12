package vpnrouter.core.service.event.annotation;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import vpnrouter.core.service.event.EventPublisher;

@Aspect
@Component
@RequiredArgsConstructor
public class EventOnSuccessHandler {

    private final EventPublisher eventPublisher;

    @AfterReturning("@annotation(EventOnSuccess)")
    @SneakyThrows
    public void publishEventOnSuccess(JoinPoint joinPoint) {
        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        var eventType = method.getAnnotation(EventOnSuccess.class).value();
        var event = eventType.getConstructor().newInstance();
        eventPublisher.publish(event);
    }
}
