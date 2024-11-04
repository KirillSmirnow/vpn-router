package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import vpnrouter.web.ui.AddClientPage;
import vpnrouter.web.ui.clients.detection.ClientDetectionComponent;
import vpnrouter.web.ui.clients.detection.ClientDetectionComponentFactory;
import vpnrouter.web.ui.clients.newclient.AddNewClientButtonFactory;
import vpnrouter.web.ui.clients.location.LocationComponentFactory;

@UIScope
@Route("")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private final ClientDetectionComponentFactory clientDetectionComponentFactory;
    private final LocationComponentFactory locationComponentFactory;
    private final AddNewClientButtonFactory addNewClientButtonFactory;

    @Override
    public void onAttach(AttachEvent event) {
        var grid = clientsGridFactory.build();
        var clientDetectionComponent = clientDetectionComponentFactory.build();
        var layout = new VerticalLayout(
                buildTopBar(clientDetectionComponent),
                new HorizontalLayout(addNewClientButtonFactory.build(), clientDetectionComponent.getStartButton()),
                clientDetectionComponent.getProgressBar(),
                grid
        );
        layout.setHeightFull();
        setContent(layout);
    }

    private HorizontalLayout buildTopBar(ClientDetectionComponent clientDetectionComponent) {
        var horizontalLayout = new HorizontalLayout(
                buildAddClientButton(),
                clientDetectionComponent.getStartButton(),
                locationComponentFactory.build().getLocationField()
        );
        horizontalLayout.setWidthFull();
        horizontalLayout.setPadding(false);
        horizontalLayout.setMargin(false);
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return horizontalLayout;
    }

    private Button buildAddClientButton() {
        var button = new Button(VaadinIcon.PLUS.create());
        button.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(AddClientPage.class)));
        return button;
    }
}
