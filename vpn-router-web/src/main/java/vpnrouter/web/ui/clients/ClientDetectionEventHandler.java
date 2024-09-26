package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.progressbar.ProgressBar;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientDetectionService;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsNotFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionFailureEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionStartedEvent;

@Component
@Getter
public class ClientDetectionEventHandler {
    private final ClientDetectionService clientDetectionService;
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public ClientDetectionEventHandler(
            ClientDetectionService clientDetectionService,
            EventSubscriberRegistry eventSubscriberRegistry
    ) {
        this.clientDetectionService = clientDetectionService;
        this.eventSubscriberRegistry = eventSubscriberRegistry;
    }

    public Button buildClientDetectionButton() {
        var clientDetectionButton = new Button(VaadinIcon.REFRESH.create());
        clientDetectionButton.addClickListener(event -> clientDetectionService.detectAndSave());
        clientDetectionButton.setEnabled(!clientDetectionService.isInProgress());
        return clientDetectionButton;
    }

    public ProgressBar buildProgressBar() {
        var progressBar = new ProgressBar();
        progressBar.setVisible(clientDetectionService.isInProgress());
        progressBar.setIndeterminate(true);
        return progressBar;
    }

    public void registerHandler(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionStartedEvent.class,
                new DetectionStartedHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsFoundEvent.class,
                new ClientsFoundHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsNotFoundEvent.class,
                new ClientsNotFoundHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionFailureEvent.class,
                new DetectionFailureHandler(UI.getCurrent(), clientDetectionButton, progressBar)
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
                    Notification.show("%d new clients found".formatted(newClientsCount));
                    clientDetectionButton.setEnabled(true);
                    progressBar.setVisible(false);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionClientsFoundEvent.class, this);
            }
        }
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
                    Notification.show("No new clients found");
                    clientDetectionButton.setEnabled(true);
                    progressBar.setVisible(false);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionClientsNotFoundEvent.class, this);
            }
        }
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
