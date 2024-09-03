package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.data.binder.Binder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.Client;

@Component
@RequiredArgsConstructor
public class ClientsGridFactory {

    private final ClientService clientService;
    private final ClientNameFieldFactory clientNameFieldFactory;
    private final ClientTunnelSwitchFactory clientTunnelSwitchFactory;
    private final ClientDeleteButtonFactory clientDeleteButtonFactory;

    public Grid<Client> build() {
        var grid = new Grid<Client>();
        var editor = createEditor(grid);
        addIpAddressField(grid);
        addNameField(grid, editor);
        addTunnelSwitch(grid);
        addDeleteButton(grid);
        refreshGrid(grid);
        return grid;
    }

    private Editor<Client> createEditor(Grid<Client> grid) {
        var editor = grid.getEditor();
        editor.setBuffered(false);
        editor.setBinder(new Binder<>(Client.class));
        grid.addItemDoubleClickListener(event -> {
            editor.editItem(event.getItem());
            var editorComponent = event.getColumn().getEditorComponent();
            if (editorComponent instanceof Focusable<?> focusableEditorComponent) {
                focusableEditorComponent.focus();
            }
        });
        return editor;
    }

    private void addIpAddressField(Grid<Client> grid) {
        grid.addColumn(Client::getIpAddress).setHeader("IP address");
    }

    private void addNameField(Grid<Client> grid, Editor<Client> editor) {
        var nameColumn = grid.addColumn(Client::getName).setHeader("Name");
        nameColumn.setEditorComponent(client -> clientNameFieldFactory.build(client, () -> {
            editor.closeEditor();
            refreshGrid(grid);
        }));
    }

    private void addTunnelSwitch(Grid<Client> grid) {
        grid.addComponentColumn(client ->
                clientTunnelSwitchFactory.build(client, () -> refreshGrid(grid))
        ).setHeader("Tunnel");
    }

    private void addDeleteButton(Grid<Client> grid) {
        grid.addComponentColumn(client ->
                clientDeleteButtonFactory.build(client, () -> refreshGrid(grid))
        );
    }

    private void refreshGrid(Grid<Client> grid) {
        grid.setItems(
                Client.from(clientService.getAll())
        );
    }
}
