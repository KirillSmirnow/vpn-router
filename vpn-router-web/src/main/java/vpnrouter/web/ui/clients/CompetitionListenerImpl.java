package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import lombok.RequiredArgsConstructor;
import vpnrouter.api.client.ClientDetectionService;

import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class CompetitionListenerImpl implements ClientDetectionService.CompletionListener {
    private final UI ui;

    @Override
    public void onStart() {
        ui.access(() -> Notification.show("New detection has started"));
    }

    @Override
    public void onAlreadyRunning() {
        ui.access(() -> Notification.show("Detection is already in progress"));
    }

    @Override
    public void onNewClientsNotFound() {
        ui.access(() -> Notification.show("Detection completed: no new clients found"));
    }

    @Override
    public void onNewClientsFound(int newClientsCount) {
        ui.access(() -> {
                    Notification.show("Detection completed: %s new clients found".formatted(newClientsCount), 10000, Notification.Position.BOTTOM_START);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ui.access(() -> ui.getPage().reload());
                        }
                    }, 2000);
                }
        );
    }

    @Override
    public void onFailure(Exception exception) {
        ui.access(() -> Notification.show("Detection error: " + exception.getMessage()));
    }
}
