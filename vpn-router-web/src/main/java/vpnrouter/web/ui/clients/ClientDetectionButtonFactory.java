package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientDetectionService;

@Component
@RequiredArgsConstructor
public class ClientDetectionButtonFactory {
    private final ClientDetectionService clientDetectionService;

    public Button build() {
        var detectionButton = new Button(VaadinIcon.REFRESH.create());
        detectionButton.addClickListener(event -> clientDetectionService.detectAndSave());
        detectionButton.setEnabled(!clientDetectionService.isInProgress());
        return detectionButton;
    }

    public boolean isDetectionInProgress() {
        return clientDetectionService.isInProgress();
    }
}
