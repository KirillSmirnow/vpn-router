package vpnrouter.core.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

@Slf4j
@Aspect
@Component
public class ServiceLogger {

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    @SneakyThrows
    public Object logInvocation(ProceedingJoinPoint joinPoint) {
        var method = joinPoint.getSignature().toShortString();
        var arguments = extractArguments(joinPoint);
        var invocation = method.replace("..", arguments);
        log.info(invocation);
        try {
            var result = joinPoint.proceed();
            log.info("{} -> {}", method, formatObject(result));
            return result;
        } catch (Exception exception) {
            log.info("{} !> {}", method, exception.getMessage());
            throw exception;
        }
    }

    private String extractArguments(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .map(this::formatObject)
                .collect(joining(", "));
    }

    private String formatObject(Object object) {
        if (object == null) {
            return "∅";
        }
        return object.toString();
    }
}
