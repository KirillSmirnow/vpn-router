package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.ClientWebView;

@CssImport("./styles/styles.css")
@UIScope
@Route("")
@Slf4j

// I advise to split this class into smaller classes (grid, toggle, name field, etc.)

@RequiredArgsConstructor
public class ClientsPage extends AppLayout {

    private final ClientService clientService;

    private final ClientNameFieldFactory clientNameFieldFactory;
    private final ClientTunnelSwitchFactory clientTunnelSwitchFactory;
    private final ClientDeleteButtonFactory clientDeleteButtonFactory;

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
        refreshGrid();
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

        addIpAddressField();
        addNameField(binder, editor);
        addTunnelSwitch();
        addDeleteButton();

        refreshGrid();
    }

    private void addIpAddressField() {
        grid.addColumn(ClientWebView::getIpAddress).setHeader("IP address");
    }

    private void addNameField(Binder<ClientWebView> binder, Editor<ClientWebView> editor) {
        var nameColumn = grid.addColumn(ClientWebView::getName).setHeader("Name");
        nameColumn.setEditorComponent(client -> clientNameFieldFactory.build(client, binder, () -> {
            editor.closeEditor();
            refreshGrid();
        }));
    }

    private void addTunnelSwitch() {
        grid.addComponentColumn(client -> clientTunnelSwitchFactory.build(client, this::refreshGrid))
                .setHeader("Tunnel");
    }

    private void addDeleteButton() {
        grid.addComponentColumn(client -> clientDeleteButtonFactory.build(client, this::refreshGrid));
    }

    private void refreshGrid() {
        grid.setItems(ClientWebView.from(clientService.getAll()));
    }
}
