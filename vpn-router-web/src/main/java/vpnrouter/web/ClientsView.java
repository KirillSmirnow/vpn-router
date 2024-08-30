package vpnrouter.web;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;

import java.util.List;

@CssImport("./styles/styles.css")
@UIScope
@Route("")
@Slf4j
public class ClientsView extends AppLayout {
    private final ClientService clientService;
    private final ClientStorage clientStorage;
    private final Grid<ClientWebView> grid;

    public ClientsView(ClientService clientService, ClientStorage clientStorage) {
        this.clientService = clientService;
        this.clientStorage = clientStorage;
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

    private void fillGrid() {
        Binder<ClientWebView> binder = new Binder<>(ClientWebView.class);
        Editor<ClientWebView> editor = grid.getEditor();
        editor.setBuffered(false);
        editor.setBinder(binder);

        grid.addItemDoubleClickListener(
                event -> {
                    editor.editItem(event.getItem());
                    Component editorComponent = event.getColumn().getEditorComponent();
                    if (editorComponent instanceof Focusable) {
                        ((Focusable<?>) editorComponent).focus();
                    }
                });
        grid.addSelectionListener(
                event -> {
                    if (event.getFirstSelectedItem().isPresent()) {
                        clientStorage.removeAll();
                        ClientWebView cachedClient = event.getFirstSelectedItem().get();
                        clientStorage.setValue(cachedClient);
                    }
                }
        );

        addIpAddressColumn();
        addNameColumn(binder, editor);
        addTunnelledToggleSwitch();

        List<ClientWebView> clients = map(clientService.getAll());
        if (!clients.isEmpty()) {
            addDeleteButton();
            grid.setItems(clients);
        }
    }

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
        Grid.Column<ClientWebView> nameColumn = grid.addColumn(ClientWebView::getName).setHeader("Name");
        TextField nameField = new TextField();
        nameField.setWidthFull();
        addCloseHandler(nameField, editor);
        binder.forField(nameField).bind(ClientWebView::getName, ClientWebView::setName);
        nameField.addValueChangeListener(
                event -> {
                    ClientWebView cachedClient = clientStorage.getValue();
                    ClientUpdate clientUpdate = ClientUpdate.builder()
                            .tunnelled(cachedClient.isTunnelled())
                            .name(event.getValue())
                            .build();
                    clientService.update(cachedClient.getIpAddress(), clientUpdate);
                }
        );
        nameColumn.setEditorComponent(nameField);
    }

    private static void addCloseHandler(Component textField, Editor<ClientWebView> editor) {
        textField.getElement()
                .addEventListener("keydown", event -> editor.cancel())
                .setFilter("event.code === 'Escape'");
    }

    private void addTunnelledToggleSwitch() {
        grid.addColumn(new ComponentRenderer<>(this::getTunnelledToggleSwitch)).setHeader("Tunnelled");
    }

    private ToggleButton getTunnelledToggleSwitch(ClientWebView client) {
        ToggleButton toggle = new ToggleButton();
        toggle.setValue(client.isTunnelled());
        toggle.addValueChangeListener(
                event -> {
                    boolean isTunnelled = event.getValue();
                    ClientUpdate clientUpdate = ClientUpdate.builder()
                            .name(client.getName())
                            .tunnelled(isTunnelled)
                            .build();
                    clientService.update(client.getIpAddress(), clientUpdate);
                }
        );
        return toggle;
    }

    private void addDeleteButton() {
        grid.addComponentColumn(
                clientView -> {
                    Button deleteButton = new Button("Delete");
                    deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    deleteButton.addClickListener(event -> getClientDeletionDialog(clientView));
                    return deleteButton;
                }
        );
    }

    private void getClientDeletionDialog(ClientWebView clientView) {
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
                    grid.setItems(map(clientService.getAll()));
                }
        );
        dialog.open();
    }
}
