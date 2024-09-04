package vpnrouter.web.exception;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.exception.ThrowableToErrorsConverter;

import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
public class GlobalErrorHandler implements VaadinServiceInitListener, ErrorHandler {

    private final ThrowableToErrorsConverter throwableToErrorsConverter;

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
        return throwableToErrorsConverter.convert(throwable).stream()
                .map(error -> (error.getField() != null ? "* " + error.getField() + ": " : "") + error.getUserMessage())
                .collect(joining("\n"));
    }
}
