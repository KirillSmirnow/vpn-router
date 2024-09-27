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
import vpnrouter.web.ui.AddClientPage;
import vpnrouter.web.ui.clients.detection.ClientDetectionComponentFactory;

@Slf4j
@UIScope
@Route("")
@CssImport("./styles/styles.css")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private final ClientDetectionComponentFactory clientDetectionComponentFactory;

    @Override
    public void onAttach(AttachEvent event) {
        var grid = clientsGridFactory.build();
        var clientDetectionComponent = clientDetectionComponentFactory.build();
        var layout = new VerticalLayout(
                new HorizontalLayout(buildAddClientButton(), clientDetectionComponent.getStartButton()),
                clientDetectionComponent.getProgressBar(),
                grid
        );
        layout.setHeightFull();
        setContent(layout);
    }

    private Button buildAddClientButton() {
        var button = new Button(VaadinIcon.PLUS.create());
        button.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(AddClientPage.class)));
        return button;
    }
}
