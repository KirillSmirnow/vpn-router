package vpnrouter.web.eventhandler;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.progressbar.ProgressBar;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsNotFoundEvent;

@Component
@RequiredArgsConstructor
public class ClientDetectionNotFoundEventHandler {
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public void register(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsNotFoundEvent.class,
                new ClientsNotFoundHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class ClientsNotFoundHandler implements EventSubscriber<ClientDetectionClientsNotFoundEvent> {
        private final UI ui;
        private final Button clientDetectionButton;
        private final ProgressBar progressBar;

        @Override
        public void receive(ClientDetectionClientsNotFoundEvent event) {
            try {
                ui.access(() -> {
                    Notification.show("Detection completed: no new clients found");
                    clientDetectionButton.setEnabled(true);
                    progressBar.setVisible(false);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionClientsNotFoundEvent.class, this);
            }
        }
    }
}
