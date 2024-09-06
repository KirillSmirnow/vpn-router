package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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
                    clientDetectionService.detectAndSave(new CompetitionListenerImpl(ui));
                }
        );
        return detectionButton;
    }
}