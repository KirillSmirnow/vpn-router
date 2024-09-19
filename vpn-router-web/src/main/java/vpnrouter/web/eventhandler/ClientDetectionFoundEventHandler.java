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
import vpnrouter.api.event.concrete.client.ClientDetectionClientsFoundEvent;

@Component
@RequiredArgsConstructor
public class ClientDetectionFoundEventHandler {
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public void register(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsFoundEvent.class,
                new ClientsFoundHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class ClientsFoundHandler implements EventSubscriber<ClientDetectionClientsFoundEvent> {
        private final UI ui;
        private final Button clientDetectionButton;
        private final ProgressBar progressBar;

        @Override
        public void receive(ClientDetectionClientsFoundEvent event) {
            try {
                var newClientsCount = event.getNewClientsCount();
                ui.access(() -> {
                            Notification.show("Detection completed: %s new clients found".formatted(newClientsCount));
                            clientDetectionButton.setEnabled(true);
                            progressBar.setVisible(false);
                        }
                );
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionClientsFoundEvent.class, this);
            }
        }
    }
}
