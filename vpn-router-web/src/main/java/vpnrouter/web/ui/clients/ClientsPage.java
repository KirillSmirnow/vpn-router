package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsNotFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionFailureEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionStartedEvent;
import vpnrouter.web.ui.AddClientPage;

@Slf4j
@UIScope
@Route("")
@CssImport("./styles/styles.css")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private final ClientDetectionButtonFactory clientDetectionButtonFactory;
    private final EventSubscriberRegistry eventSubscriberRegistry;

    @Override
    public void onAttach(AttachEvent event) {
        var grid = clientsGridFactory.build();
        var clientDetectionButton = buildDetectClientButton();
        var buttons = new HorizontalLayout(buildAddClientButton(), clientDetectionButton);
        var layout = new VerticalLayout(buttons, grid);
        registerDetectionStartedHandler(clientDetectionButton);
        registerClientsNotFoundHandler(clientDetectionButton);
        registerClientsFoundHandler(clientDetectionButton);
        registerDetectionFailedHandler(clientDetectionButton);
        layout.setHeightFull();
        setContent(layout);
    }

    private Button buildAddClientButton() {
        var button = new Button(VaadinIcon.PLUS.create());
        button.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(AddClientPage.class)));
        return button;
    }

    private Button buildDetectClientButton() {
        var button = clientDetectionButtonFactory.build();
        button.setEnabled(!clientDetectionButtonFactory.isDetectionInProgress());
        return button;
    }

    private void registerDetectionStartedHandler(Button clientDetectionButton) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionStartedEvent.class,
                new DetectionStartedHandler(UI.getCurrent(), clientDetectionButton)
        );
    }

    private void registerClientsNotFoundHandler(Button clientDetectionButton) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsNotFoundEvent.class,
                new ClientsNotFoundHandler(UI.getCurrent(), clientDetectionButton)
        );
    }

    private void registerClientsFoundHandler(Button clientDetectionButton) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsFoundEvent.class,
                new ClientsFoundHandler(UI.getCurrent(), clientDetectionButton)
        );
    }

    private void registerDetectionFailedHandler(Button clientDetectionButton) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionFailureEvent.class,
                new DetectionFailureHandler(UI.getCurrent(), clientDetectionButton)
        );
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class ClientsNotFoundHandler implements EventSubscriber<ClientDetectionClientsNotFoundEvent> {
        private final UI ui;
        private final Button clientDetectionButton;

        @Override
        public void receive(ClientDetectionClientsNotFoundEvent event) {
            try {
                ui.access(() -> {
                            Notification.show("Detection completed: no new clients found");
                            clientDetectionButton.setEnabled(true);
                        }
                );
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionClientsNotFoundEvent.class, this);
            }
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class ClientsFoundHandler implements EventSubscriber<ClientDetectionClientsFoundEvent> {
        private final UI ui;
        private final Button clientDetectionButton;

        @Override
        public void receive(ClientDetectionClientsFoundEvent event) {
            try {
                var newClientsCount = event.getNewClientsCount();
                ui.access(() -> {
                            Notification.show("Detection completed: %s new clients found".formatted(newClientsCount));
                            clientDetectionButton.setEnabled(true);
                        }
                );
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionClientsFoundEvent.class, this);
            }
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class DetectionFailureHandler implements EventSubscriber<ClientDetectionFailureEvent> {
        private final UI ui;
        private final Button clientDetectionButton;

        @Override
        public void receive(ClientDetectionFailureEvent event) {
            try {
                var exception = event.getException();
                ui.access(() -> {
                    Notification.show("Detection error: " + exception.getMessage());
                    clientDetectionButton.setEnabled(true);
                });
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionFailureEvent.class, this);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class DetectionStartedHandler implements EventSubscriber<ClientDetectionStartedEvent> {
        private final UI ui;
        private final Button clientDetectionButton;

        @Override
        public void receive(ClientDetectionStartedEvent event) {
            try {
                ui.access(() -> clientDetectionButton.setEnabled(false));
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionStartedEvent.class, this);
            }
        }
    }
}
