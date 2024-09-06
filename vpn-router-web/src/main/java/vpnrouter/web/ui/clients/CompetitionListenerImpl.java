package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import vpnrouter.api.client.ClientDetectionService;


public class CompetitionListenerImpl implements ClientDetectionService.CompletionListener {
    private final UI ui;

    public CompetitionListenerImpl(UI ui) {
        this.ui = ui;
    }

    @Override
    public void onStart() {
        ui.access(() -> Notification.show("Client detection has started"));
    }

    @Override
    public void onAlreadyRunning() {
        ui.access(() -> Notification.show("Detection is already running"));
    }

    @Override
    public void onNewClientsNotFound() {
        ui.access(() -> Notification.show("Detection completed: new clients not found"));
    }

    @Override
    public void onNewClientsFound(int newClientsCount) {
        ui.access(() ->
                Notification.show("Detection completed: %s new clients found".formatted(newClientsCount)));
    }

    @Override
    public void onFailure(Exception exception) {
        ui.access(() -> Notification.show("Detection failure: " + exception.getMessage()));
    }
}
