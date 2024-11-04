package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import vpnrouter.web.ui.clients.detection.ClientDetectionComponent;
import vpnrouter.web.ui.clients.detection.ClientDetectionComponentFactory;
import vpnrouter.web.ui.clients.location.LocationComponentFactory;
import vpnrouter.web.ui.clients.newclient.AddClientButtonFactory;

@UIScope
@Route("")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private final ClientDetectionComponentFactory clientDetectionComponentFactory;
    private final LocationComponentFactory locationComponentFactory;
    private final AddClientButtonFactory addClientButtonFactory;

    @Override
    public void onAttach(AttachEvent event) {
        var grid = clientsGridFactory.build();
        var clientDetectionComponent = clientDetectionComponentFactory.build();
        var layout = new VerticalLayout(
                buildTopBar(clientDetectionComponent),
                clientDetectionComponent.getProgressBar(),
                grid
        );
        layout.setHeightFull();
        setContent(layout);
    }

    private HorizontalLayout buildTopBar(ClientDetectionComponent clientDetectionComponent) {
        var horizontalLayout = new HorizontalLayout(
                addClientButtonFactory.build(),
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
}
