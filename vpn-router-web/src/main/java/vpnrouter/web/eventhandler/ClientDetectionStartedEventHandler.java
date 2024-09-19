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
import vpnrouter.api.event.concrete.client.ClientDetectionStartedEvent;

@Component
@RequiredArgsConstructor
public class ClientDetectionStartedEventHandler {
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public void register(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionStartedEvent.class,
                new DetectionStartedHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class DetectionStartedHandler implements EventSubscriber<ClientDetectionStartedEvent> {
        private final UI ui;
        private final Button clientDetectionButton;
        private final ProgressBar progressBar;

        @Override
        public void receive(ClientDetectionStartedEvent event) {
            try {
                ui.access(() -> {
                    clientDetectionButton.setEnabled(false);
                    progressBar.setVisible(true);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionStartedEvent.class, this);
            }
        }
    }
}
