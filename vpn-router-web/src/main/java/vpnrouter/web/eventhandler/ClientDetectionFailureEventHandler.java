package vpnrouter.web.eventhandler;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.progressbar.ProgressBar;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.api.event.concrete.client.ClientDetectionFailureEvent;

@Component
@RequiredArgsConstructor
public class ClientDetectionFailureEventHandler {
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public void register(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionFailureEvent.class,
                new DetectionFailureHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class DetectionFailureHandler implements EventSubscriber<ClientDetectionFailureEvent> {
        private final UI ui;
        private final Button clientDetectionButton;
        private final ProgressBar progressBar;

        @Override
        public void receive(ClientDetectionFailureEvent event) {
            try {
                var exception = event.getException();
                ui.access(() -> {
                    clientDetectionButton.setEnabled(true);
                    progressBar.setVisible(false);
                    throw new RuntimeException(exception);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionFailureEvent.class, this);
            }
        }
    }
}
