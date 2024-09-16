package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientDetectionService;


@Component
@RequiredArgsConstructor
@CssImport("./styles/styles.css")
public class SpinnerFactory {
    private final ClientDetectionService clientDetectionService;

    public Icon build() {
        var spinner = new Icon(VaadinIcon.HOURGLASS);
        spinner.addClassName("hourglass-spinner");
        spinner.setVisible(clientDetectionService.isInProgress());
        return spinner;
    }
}
