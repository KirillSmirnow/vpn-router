package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import vpnrouter.web.ui.clients.detection.ClientDetectionComponentFactory;
import vpnrouter.web.ui.clients.newclient.AddNewClientButtonFactory;

@UIScope
@Route("")
@CssImport("./styles/styles.css")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private final ClientDetectionComponentFactory clientDetectionComponentFactory;
    private final AddNewClientButtonFactory addNewClientButtonFactory;

    @Override
    public void onAttach(AttachEvent event) {
        var grid = clientsGridFactory.build();
        var clientDetectionComponent = clientDetectionComponentFactory.build();
        var layout = new VerticalLayout(
                new HorizontalLayout(addNewClientButtonFactory.build(), clientDetectionComponent.getStartButton()),
                clientDetectionComponent.getProgressBar(),
                grid
        );
        layout.setHeightFull();
        setContent(layout);
    }
}
