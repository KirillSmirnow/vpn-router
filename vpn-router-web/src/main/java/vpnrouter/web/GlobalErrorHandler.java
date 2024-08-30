package vpnrouter.web;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.joining;

@Component
public class GlobalErrorHandler implements VaadinServiceInitListener, ErrorHandler {

    @Override
    public void serviceInit(ServiceInitEvent serviceInitialization) {
        serviceInitialization.getSource().addSessionInitListener(sessionInitialization ->
                sessionInitialization.getSession().setErrorHandler(this)
        );
    }

    @Override
    public void error(ErrorEvent event) {
        var message = buildMessage(event.getThrowable());
        var notification = new Notification(message);
        notification.setDuration(10_000);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    private String buildMessage(Throwable throwable) {
        if (throwable instanceof ConstraintViolationException exception) {
            return buildMessage(exception);
        }
        return "Unexpected error: " + throwable.getMessage();
    }

    private String buildMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(violation -> "* %s: %s".formatted(violation.getPropertyPath(), violation.getMessage()))
                .sorted()
                .collect(joining("\r\n"));
    }
}
