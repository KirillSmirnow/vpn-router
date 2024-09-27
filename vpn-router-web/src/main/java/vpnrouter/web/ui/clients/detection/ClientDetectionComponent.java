package vpnrouter.web.ui.clients.detection;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.progressbar.ProgressBar;
import lombok.Getter;

@Getter
public class ClientDetectionComponent {

    private final Button startButton;
    private final ProgressBar progressBar;

    public ClientDetectionComponent(State initialState, Runnable onDetectionStartListener) {
        this.startButton = buildStartButton(onDetectionStartListener);
        this.progressBar = buildProgressBar();
        setState(initialState);
    }

    private Button buildStartButton(Runnable onDetectionStartListener) {
        var clientDetectionButton = new Button(VaadinIcon.REFRESH.create());
        clientDetectionButton.addClickListener(event -> onDetectionStartListener.run());
        return clientDetectionButton;
    }

    private ProgressBar buildProgressBar() {
        var progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        return progressBar;
    }

    public void setState(State state) {
        startButton.setEnabled(state == State.IDLE);
        progressBar.setVisible(state == State.IN_PROGRESS);
    }

    public enum State {
        IDLE,
        IN_PROGRESS,
    }
}
