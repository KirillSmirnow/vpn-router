package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import lombok.RequiredArgsConstructor;
import vpnrouter.api.client.ClientDetectionService;

@RequiredArgsConstructor
public class StartListenerImpl implements ClientDetectionService.StartListener {
    private final UI ui;

    @Override
    public void onStart() {
        ui.access(() -> Notification.show("New detection has started"));
    }
}
