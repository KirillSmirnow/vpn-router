package vpnrouter.web;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.grid.editor.EditorSaveEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;

import java.util.*;

@CssImport("./styles/styles.css")
@UIScope
@Route("clients")
@Component
public class ClientsList extends AppLayout {
    private final ClientService clientService;
    private final Grid<ClientWebView> grid;
    private static Map<String, ClientWebView> c = new HashMap<>();

    public ClientsList(ClientService clientService) {
        this.clientService = clientService;
        VerticalLayout layout = new VerticalLayout();
        grid = new Grid<>();
        addToNavbar(new H3("Clients"));
        setContent(layout);
        Button addClientButton = new Button(
                "Add client",
                event -> getUI().ifPresent(ui -> ui.navigate("/clients/add"))
        );
        layout.add(grid, addClientButton);
        fillGrid();
    }

    @Override
    public void onAttach(AttachEvent event) {
        List<ClientWebView> clients = map(clientService.getAll());
        grid.setItems(clients);
    }

    private void fillGrid() {
        Binder<ClientWebView> binder = new Binder<>(ClientWebView.class);
        addIpAddressColumn();
        Editor<ClientWebView> editor = grid.getEditor();
        editor.setBuffered(false);
        editor.setBinder(binder);
        editor.addSaveListener(this::updateClient);
        addNameColumn(binder, editor);
        addTunnelledToggleSwitch();
        grid.addItemDoubleClickListener(e -> {
            editor.editItem(e.getItem());
            com.vaadin.flow.component.Component editorComponent = e.getColumn().getEditorComponent();
            if (editorComponent instanceof Focusable) {
                ((Focusable) editorComponent).focus();
            }
        });

        List<ClientWebView> clients = map(clientService.getAll());
        if (!clients.isEmpty()) {
            addDeleteButton();
            grid.setItems(clients);
        }
    }

    private List<ClientWebView> map(List<ClientView> clients) {
        return clients
                .stream()
                .map(client -> new ClientWebView(client.getIpAddress(), client.getName(), client.isTunnelled()))
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
        nameColumn.setEditorComponent(nameField);
        grid.addSelectionListener(event -> {
            try {
                ClientWebView client = event.getFirstSelectedItem().get();
                c.put("s", client);
                System.out.println("D");
            } catch (NoSuchElementException e) {
                System.out.println("E");
            }
        });
        nameField.addValueChangeListener(
                event -> {
                    String name = event.getValue();
                    String ipAddress = c.get("s").getIpAddress();
                    boolean isTunnelled = c.get("s").isTunnelled();
                    ClientUpdate clientUpdate = ClientUpdate.builder()
                            .tunnelled(isTunnelled)
                            .name(name)
                            .build();
                    clientService.update(ipAddress, clientUpdate);
                    System.out.println("D");
                }
        );
    }

    private static void addCloseHandler(com.vaadin.flow.component.Component textField, Editor<ClientWebView> editor) {
        textField.getElement().addEventListener("keydown", e -> editor.cancel())
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

    private void updateClient(EditorSaveEvent<ClientWebView> event) {
        ClientWebView editedClientView = event.getItem();
        ClientUpdate clientUpdate = ClientUpdate.builder()
                .name(editedClientView.getName())
                .tunnelled(editedClientView.isTunnelled())
                .build();
        clientService.update(editedClientView.getIpAddress(), clientUpdate);
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
