package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.data.binder.Binder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.ClientWebView;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ClientsGridFactory {

    private final ClientService clientService;
    private final ClientNameFieldFactory clientNameFieldFactory;
    private final ClientTunnelSwitchFactory clientTunnelSwitchFactory;
    private final ClientDeleteButtonFactory clientDeleteButtonFactory;

    public Grid<ClientWebView> build(Consumer<Runnable> refresherProvider) {
        var grid = new Grid<ClientWebView>();
        var editor = grid.getEditor();
        editor.setBuffered(false);
        var binder = new Binder<>(ClientWebView.class);
        editor.setBinder(binder);

        // strange behavior: when you click on an IP address, name text field becomes editable
        grid.addItemDoubleClickListener(event -> {
            editor.editItem(event.getItem());
            com.vaadin.flow.component.Component editorComponent = event.getColumn().getEditorComponent();
            if (editorComponent instanceof Focusable) {
                ((Focusable<?>) editorComponent).focus();
            }
        });

        addIpAddressField(grid);
        addNameField(grid, binder, editor);
        addTunnelSwitch(grid);
        addDeleteButton(grid);

        refresherProvider.accept(() -> refreshGrid(grid));

        return grid;
    }

    private void addIpAddressField(Grid<ClientWebView> grid) {
        grid.addColumn(ClientWebView::getIpAddress).setHeader("IP address");
    }

    private void addNameField(Grid<ClientWebView> grid, Binder<ClientWebView> binder, Editor<ClientWebView> editor) {
        var nameColumn = grid.addColumn(ClientWebView::getName).setHeader("Name");
        nameColumn.setEditorComponent(client -> clientNameFieldFactory.build(client, binder, () -> {
            editor.closeEditor();
            refreshGrid(grid);
        }));
    }

    private void addTunnelSwitch(Grid<ClientWebView> grid) {
        grid.addComponentColumn(client ->
                clientTunnelSwitchFactory.build(client, () -> refreshGrid(grid))
        ).setHeader("Tunnel");
    }

    private void addDeleteButton(Grid<ClientWebView> grid) {
        grid.addComponentColumn(client ->
                clientDeleteButtonFactory.build(client, () -> refreshGrid(grid))
        );
    }

    private void refreshGrid(Grid<ClientWebView> grid) {
        grid.setItems(
                ClientWebView.from(clientService.getAll())
        );
    }
}
