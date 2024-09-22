package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.progressbar.ProgressBar;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import vpnrouter.api.client.ClientDetectionService;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsNotFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionFailureEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionStartedEvent;

public class ClientDetectionEventHandler {
    private final Button clientDetectionButton;
    private final ProgressBar progressBar;
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public ClientDetectionEventHandler(
            ClientDetectionService clientDetectionService,
            EventSubscriberRegistry eventSubscriberRegistry,
            Button clientDetectionButton,
            ProgressBar progressBar
    ) {
        this.eventSubscriberRegistry = eventSubscriberRegistry;
        this.clientDetectionButton = clientDetectionButton;
        clientDetectionButton.addClickListener(event -> clientDetectionService.detectAndSave());
        clientDetectionButton.setEnabled(!clientDetectionService.isInProgress());
        this.progressBar = progressBar;
        progressBar.setVisible(clientDetectionService.isInProgress());
        progressBar.setIndeterminate(true);
    }

    public void registerHandler() {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionStartedEvent.class,
                new DetectionStartedHandler(UI.getCurrent())
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsFoundEvent.class,
                new ClientsFoundHandler(UI.getCurrent())
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsNotFoundEvent.class,
                new ClientsNotFoundHandler(UI.getCurrent())
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionFailureEvent.class,
                new DetectionFailureHandler(UI.getCurrent())
        );
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class DetectionStartedHandler implements EventSubscriber<ClientDetectionStartedEvent> {
        private final UI ui;

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
