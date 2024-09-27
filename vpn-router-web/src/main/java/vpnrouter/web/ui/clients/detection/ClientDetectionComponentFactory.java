package vpnrouter.web.ui.clients.detection;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.notification.Notification;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientDetectionService;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsNotFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionFailureEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionStartedEvent;
import vpnrouter.web.ui.clients.detection.ClientDetectionComponent.State;

@Component
@RequiredArgsConstructor
public class ClientDetectionComponentFactory {

    private final ClientDetectionService clientDetectionService;
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public ClientDetectionComponent build() {
        var initialState = clientDetectionService.isInProgress() ? State.IN_PROGRESS : State.IDLE;
        var clientDetectionComponent = new ClientDetectionComponent(initialState, clientDetectionService::detectAndSave);
        registerHandlers(clientDetectionComponent);
        return clientDetectionComponent;
    }

    private void registerHandlers(ClientDetectionComponent clientDetectionComponent) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionStartedEvent.class,
                new DetectionStartedEventHandler(UI.getCurrent(), clientDetectionComponent)
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsFoundEvent.class,
                new ClientsFoundEventHandler(UI.getCurrent(), clientDetectionComponent)
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsNotFoundEvent.class,
                new ClientsNotFoundEventHandler(UI.getCurrent(), clientDetectionComponent)
        );
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionFailureEvent.class,
                new DetectionFailureEventHandler(UI.getCurrent(), clientDetectionComponent)
        );
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class DetectionStartedEventHandler implements EventSubscriber<ClientDetectionStartedEvent> {

        private final UI ui;
        private final ClientDetectionComponent clientDetectionComponent;

        @Override
        public void receive(ClientDetectionStartedEvent event) {
            try {
                ui.access(() -> {
                    clientDetectionComponent.setState(State.IN_PROGRESS);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionStartedEvent.class, this);
            }
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class ClientsFoundEventHandler implements EventSubscriber<ClientDetectionClientsFoundEvent> {

        private final UI ui;
        private final ClientDetectionComponent clientDetectionComponent;

        @Override
        public void receive(ClientDetectionClientsFoundEvent event) {
            try {
                var newClientsCount = event.getNewClientsCount();
                ui.access(() -> {
                    Notification.show("%d new clients found".formatted(newClientsCount));
                    clientDetectionComponent.setState(State.IDLE);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionClientsFoundEvent.class, this);
            }
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class ClientsNotFoundEventHandler implements EventSubscriber<ClientDetectionClientsNotFoundEvent> {

        private final UI ui;
        private final ClientDetectionComponent clientDetectionComponent;

        @Override
        public void receive(ClientDetectionClientsNotFoundEvent event) {
            try {
                ui.access(() -> {
                    Notification.show("No new clients found");
                    clientDetectionComponent.setState(State.IDLE);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionClientsNotFoundEvent.class, this);
            }
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class DetectionFailureEventHandler implements EventSubscriber<ClientDetectionFailureEvent> {

        private final UI ui;
        private final ClientDetectionComponent clientDetectionComponent;

        @Override
        public void receive(ClientDetectionFailureEvent event) {
            try {
                var exception = event.getException();
                ui.access(() -> {
                    clientDetectionComponent.setState(State.IDLE);
                    throw new RuntimeException(exception);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionFailureEvent.class, this);
            }
        }
    }
}
