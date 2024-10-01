package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import vpnrouter.web.ui.AddClientPage;
import vpnrouter.web.ui.clients.detection.ClientDetectionComponentFactory;
import vpnrouter.web.ui.clients.location.LocationComponentFactory;

import java.util.concurrent.CompletableFuture;

@UIScope
@Route("")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private final ClientDetectionComponentFactory clientDetectionComponentFactory;
    private final LocationComponentFactory locationComponentFactory;

    @Override
    public void onAttach(AttachEvent event) {
        fetchIpAddress().thenAccept(ipAddress -> {
            var grid = clientsGridFactory.build();
            var clientDetectionComponent = clientDetectionComponentFactory.build();
            var locationComponent = locationComponentFactory.build(ipAddress);
            var buttonsLayout = new HorizontalLayout(buildAddClientButton(), clientDetectionComponent.getStartButton());
            var locationLayout = locationComponent.getLayout();
            var layout = new VerticalLayout(
                    new HorizontalLayout(buttonsLayout, locationLayout),
                    clientDetectionComponent.getProgressBar(),
                    grid
            );
            layout.setHeightFull();
            setContent(layout);
        });
    }

    private Button buildAddClientButton() {
        var button = new Button(VaadinIcon.PLUS.create());
        button.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate(AddClientPage.class)));
        return button;
    }

    private CompletableFuture<String> fetchIpAddress() {
        var completableFuture = new CompletableFuture<String>();
        UI.getCurrent().getPage().executeJs("""
                      var request = new XMLHttpRequest();
                      request.open("GET", "http://ip.cucurum.ru", false); // false for synchronous request
                      request.send(null);
                      return request.responseText;
                """).then(result -> completableFuture.complete(result.asString()));
        return completableFuture;
    }
}
