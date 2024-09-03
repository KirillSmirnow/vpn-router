package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;
import vpnrouter.web.model.ClientWebView;

import java.util.List;

@CssImport("./styles/styles.css")
@UIScope
@Route("")
@Slf4j

// I advise to split this class into smaller classes (grid, toggle, name field, etc.)

@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientService clientService;

    private final ClientTunnelledSwitch clientTunnelledSwitch;
    private final ClientDeletion clientDeletion;

    private Grid<ClientWebView> grid;

    @PostConstruct
    public void initialize() {
        VerticalLayout layout = new VerticalLayout();
        grid = new Grid<>();
        addToNavbar(new H3("Clients"));
        setContent(layout);
        Button addClientButton = getAddClientButton();
        layout.add(grid, addClientButton);
        fillGrid();
    }

    private Button getAddClientButton() {
        return new Button(
                "Add client",
                event -> getUI().ifPresent(ui -> ui.navigate("/clients/add"))
        );
    }

    @Override
    public void onAttach(AttachEvent event) {
        List<ClientWebView> clients = map(clientService.getAll());
        grid.setItems(clients);
    }

    // bad name: it not only fills, it also sets listeners...
    private void fillGrid() {
        Binder<ClientWebView> binder = new Binder<>(ClientWebView.class);
        Editor<ClientWebView> editor = grid.getEditor();
        editor.setBuffered(false);
        editor.setBinder(binder);

        // strange behavior: when you click on an IP address, name text field becomes editable
        grid.addItemDoubleClickListener(
                event -> {
                    editor.editItem(event.getItem());
                    Component editorComponent = event.getColumn().getEditorComponent();
                    if (editorComponent instanceof Focusable) {
                        ((Focusable<?>) editorComponent).focus();
                    }
                });

        addIpAddressColumn();
        addNameColumn(binder, editor);
        addTunnelledToggleSwitch();

        List<ClientWebView> clients = map(clientService.getAll());
        if (!clients.isEmpty()) {
            addDeleteButton();
            grid.setItems(clients);
        }
    }

    // unclear name
    private List<ClientWebView> map(List<ClientView> clients) {
        return clients
                .stream()
                .map(
                        client -> ClientWebView.builder()
                                .ipAddress(client.getIpAddress())
                                .name(client.getName())
                                .tunnelled(client.isTunnelled())
                                .build()
                )
                .toList();
    }

    private void addIpAddressColumn() {
        grid.addColumn(ClientWebView::getIpAddress).setHeader("IP address");
    }

    private void addNameColumn(Binder<ClientWebView> binder, Editor<ClientWebView> editor) {
        var nameColumn = grid.addColumn(ClientWebView::getName).setHeader("Name");
        nameColumn.setEditorComponent(client -> {
            var nameField = new TextField();
            nameField.setWidthFull();
            binder.forField(nameField).bind(ClientWebView::getName, ClientWebView::setName);

            nameField.addKeyDownListener(Key.ENTER, event -> {
                ClientUpdate clientUpdate = ClientUpdate.builder()
                        .tunnelled(client.isTunnelled())
                        .name(((TextField) event.getSource()).getValue())
                        .build();
                clientService.update(client.getIpAddress(), clientUpdate);
                fillGrid();
                editor.closeEditor();
            });
            nameField.addKeyDownListener(Key.ESCAPE, event -> {
                fillGrid();
                editor.closeEditor();
            });

            return nameField;
        });
    }

    private void addTunnelledToggleSwitch() {
        grid.addComponentColumn(client -> clientTunnelledSwitch.buildSwitch(client, this::fillGrid))
                .setHeader("Tunnelled");
    }

    private void addDeleteButton() {
        grid.addComponentColumn(client -> clientDeletion.buildDeleteButton(client, this::fillGrid));
    }
}
