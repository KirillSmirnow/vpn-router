package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import vpnrouter.api.client.ClientDetectionService;

public class CompetitionListenerImpl implements ClientDetectionService.CompletionListener {
    @Override
    public void onAlreadyRunning() {
        UI.getCurrent().access(() -> Notification.show("Detection is already running"));
    }

    @Override
    public void onNewClientsNotFound() {
        UI.getCurrent().access(() -> Notification.show("Detection completed: new clients not found"));
    }

    @Override
    public void onNewClientsFound(int newClientsCount) {
        UI.getCurrent().access(() ->
                Notification.show("Detection completed: %s new clients found".formatted(newClientsCount)));
    }

    @Override
    public void onFailure(Exception exception) {
        UI.getCurrent().access(() -> Notification.show("Detection failure: " + exception.getMessage()));
    }
}
