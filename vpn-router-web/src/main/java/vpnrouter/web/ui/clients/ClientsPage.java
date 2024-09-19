package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vpnrouter.web.eventhandler.ClientDetectionFailureEventHandler;
import vpnrouter.web.eventhandler.ClientDetectionFoundEventHandler;
import vpnrouter.web.eventhandler.ClientDetectionNotFoundEventHandler;
import vpnrouter.web.eventhandler.ClientDetectionStartedEventHandler;
import vpnrouter.web.ui.AddClientPage;

@Slf4j
@UIScope
@Route("")
@CssImport("./styles/styles.css")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private final ClientDetectionButtonFactory clientDetectionButtonFactory;
    private final ProgressBarFactory progressBarFactory;
    private final ClientDetectionStartedEventHandler clientDetectionStartedEventHandler;
    private final ClientDetectionFoundEventHandler clientDetectionFoundEventHandler;
    private final ClientDetectionNotFoundEventHandler clientDetectionNotFoundEventHandler;
    private final ClientDetectionFailureEventHandler clientDetectionFailureEventHandler;

    @Override
    public void onAttach(AttachEvent event) {
        var grid = clientsGridFactory.build();
        var clientDetectionButton = clientDetectionButtonFactory.build();
        var progressBar = progressBarFactory.build();
        var buttons = new HorizontalLayout(buildAddClientButton(), clientDetectionButton, progressBar);
        var layout = new VerticalLayout(buttons, progressBar, grid);
        clientDetectionStartedEventHandler.register(clientDetectionButton, progressBar);
        clientDetectionNotFoundEventHandler.register(clientDetectionButton, progressBar);
        clientDetectionFoundEventHandler.register(clientDetectionButton, progressBar);
        clientDetectionFailureEventHandler.register(clientDetectionButton, progressBar);
        layout.setHeightFull();
        setContent(layout);
    }

    private Button buildAddClientButton() {
        var button = new Button(VaadinIcon.PLUS.create());
        button.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(AddClientPage.class)));
        return button;
    }
}
