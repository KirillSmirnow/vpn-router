package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import vpnrouter.web.ui.AddClientPage;

@UIScope
@Route("")
@PageTitle("Clients")
@CssImport("./styles/styles.css")
@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientsGridFactory clientsGridFactory;
    private Runnable clientsGridRefresher;

    @PostConstruct
    public void initialize() {
        var grid = clientsGridFactory.build((clientsGridRefresher) ->
                this.clientsGridRefresher = clientsGridRefresher
        );
        var layout = new VerticalLayout(grid, buildAddClientButton());
        layout.setHeightFull();
        setContent(layout);
        addToNavbar(new H3("Clients"));
        clientsGridRefresher.run();
    }

    private Button buildAddClientButton() {
        return new Button(
                "Add client",
                event -> getUI().ifPresent(ui -> ui.navigate(AddClientPage.class))
        );
    }

    @Override
    public void onAttach(AttachEvent event) {
        clientsGridRefresher.run();
    }
}
