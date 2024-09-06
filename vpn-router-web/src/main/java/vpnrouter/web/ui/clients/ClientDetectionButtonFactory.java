package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientDetectionService;

@Component
@RequiredArgsConstructor
public class ClientDetectionButtonFactory {
    private final ClientDetectionService clientDetectionService;

    public Button build() {
        var detectionButton = new Button("Detect clients");
        detectionButton.addClickListener(event -> {
                    UI ui = event.getSource().getUI().orElseThrow();
                    clientDetectionService.detectAndSave(
                            new ClientDetectionService.CompletionListener() {
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
                    );
                }
        );
        return detectionButton;
    }
}