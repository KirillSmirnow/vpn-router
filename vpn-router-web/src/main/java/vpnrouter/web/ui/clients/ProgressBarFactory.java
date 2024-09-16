package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.progressbar.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientDetectionService;

@Component
@RequiredArgsConstructor
public class ProgressBarFactory {
    private final ClientDetectionService clientDetectionService;

    public ProgressBar build() {
        var progressBar = new ProgressBar();
        progressBar.setVisible(clientDetectionService.isInProgress());
        progressBar.setIndeterminate(true);
        return progressBar;
    }
}
