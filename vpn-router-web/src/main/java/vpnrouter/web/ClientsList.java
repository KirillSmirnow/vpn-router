package vpnrouter.web;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientView;

import java.util.List;

@UIScope
@Route("clients")
@Component
public class ClientsList extends AppLayout {
    private final ClientService clientService;
    private VerticalLayout layout;
    private Grid<ClientView> grid;

    public ClientsList(ClientService clientService) {
        this.clientService = clientService;
        layout = new VerticalLayout();
        grid = new Grid<>();
        layout.add(grid);
        addToNavbar(new H3("Clients"));
        setContent(layout);
    }

    @PostConstruct
    public void fillGrid() {
        List<ClientView> clients = clientService.getAll();
        if (!clients.isEmpty()) {
            grid.addColumn(ClientView::getIpAddress).setHeader("Ip address");
            grid.addColumn(ClientView::getName).setHeader("Name");
            grid.addColumn(ClientView::isTunnelled).setHeader("Tunnelled");
            grid.addColumn(deleteButton());
            grid.setItems(clients);
        }
    }

    private NativeButtonRenderer<ClientView> deleteButton() {
        return new NativeButtonRenderer<>(
                "Delete",
                clientView -> {
                    ConfirmDialog dialog = getClientDeletionDialog(clientView);
                    dialog.open();
                }
        );
    }

    private ConfirmDialog getClientDeletionDialog(ClientView clientView) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete client");
        dialog.setText("Do you really want to delete %s client?".formatted(clientView.getName()));
        dialog.setCancelable(true);
        dialog.addCancelListener(event -> dialog.close());
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(
                event -> {
                    clientService.remove(clientView.getIpAddress());
                    grid.setItems(clientService.getAll());
                }
        );
        return dialog;
    }

}
