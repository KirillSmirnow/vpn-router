package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vpnrouter.api.client.ClientDetectionService;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.web.ui.AddClientPage;

@Slf4j
@UIScope
@Route("")
@CssImport("./styles/styles.css")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private final ClientDetectionService clientDetectionService;
    private final EventSubscriberRegistry eventSubscriberRegistry;

    @Override
    public void onAttach(AttachEvent event) {
        var grid = clientsGridFactory.build();
        var clientDetectionButton = new Button(VaadinIcon.REFRESH.create());
        var progressBar = new ProgressBar();
        var buttons = new HorizontalLayout(buildAddClientButton(), clientDetectionButton);
        var layout = new VerticalLayout(buttons, progressBar, grid);
        layout.setHeightFull();
        var clientDetectionEventHandler = new ClientDetectionEventHandler(
                clientDetectionService,
                eventSubscriberRegistry,
                clientDetectionButton,
                progressBar
        );
        clientDetectionEventHandler.registerHandler();
        setContent(layout);
    }

    private Button buildAddClientButton() {
        var button = new Button(VaadinIcon.PLUS.create());
        button.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(AddClientPage.class)));
        return button;
    }
}
