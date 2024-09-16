package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.ErrorEvent;
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
        var progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        var buttons = new HorizontalLayout(buildAddClientButton(), clientDetectionButton, progressBar);
        var layout = new VerticalLayout(buttons, progressBar, grid);
        registerDetectionStartedHandler(clientDetectionButton, progressBar);
        registerClientsNotFoundHandler(clientDetectionButton, progressBar);
        registerClientsFoundHandler(clientDetectionButton, progressBar);
        registerDetectionFailedHandler(clientDetectionButton, progressBar);
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

    private void registerDetectionStartedHandler(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionStartedEvent.class,
                new DetectionStartedHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
    }

    private void registerClientsNotFoundHandler(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsNotFoundEvent.class,
                new ClientsNotFoundHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
    }

    private void registerClientsFoundHandler(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionClientsFoundEvent.class,
                new ClientsFoundHandler(UI.getCurrent(), clientDetectionButton, progressBar)
        );
    }

    private void registerDetectionFailedHandler(Button clientDetectionButton, ProgressBar progressBar) {
        eventSubscriberRegistry.addSubscriber(
                ClientDetectionFailureEvent.class,
                new DetectionFailureHandler(UI.getCurrent(), clientDetectionButton, progressBar)
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

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class DetectionStartedHandler implements EventSubscriber<ClientDetectionStartedEvent> {
        private final UI ui;
        private final Button clientDetectionButton;
        private final ProgressBar progressBar;

        @Override
        public void receive(ClientDetectionStartedEvent event) {
            try {
                ui.access(() -> {clientDetectionButton.setEnabled(false); progressBar.setVisible(true);});
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(ClientDetectionStartedEvent.class, this);
            }
        }
    }
}
