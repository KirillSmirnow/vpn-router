package vpnrouter.web.ui.clients;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.binder.Binder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.api.event.concrete.GeneralUpdateEvent;
import vpnrouter.web.model.Client;
import vpnrouter.web.utility.UiUtility;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientsGridFactory {

    private final ClientService clientService;
    private final ClientNameFieldFactory clientNameFieldFactory;
    private final ClientTunnelSwitchFactory clientTunnelSwitchFactory;
    private final ClientDeleteButtonFactory clientDeleteButtonFactory;
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public Grid<Client> build() {
        var grid = new Grid<Client>();
        createEditor(grid);
        addIpAddressField(grid);
        addNameField(grid);
        addTunnelSwitch(grid);
        addDeleteButton(grid);
        registerUpdateHandler(grid);
        refreshGrid(grid);
        return grid;
    }

    private void createEditor(Grid<Client> grid) {
        var editor = grid.getEditor();
        editor.setBinder(new Binder<>());
        grid.addItemDoubleClickListener(event -> editor.editItem(event.getItem()));
    }

    private void addIpAddressField(Grid<Client> grid) {
        grid.addColumn(Client::getIpAddress).setHeader("IP address");
    }

    private void addNameField(Grid<Client> grid) {
        var nameColumn = grid.addColumn(Client::getName).setHeader("Name");
        nameColumn.setEditorComponent(client ->
                clientNameFieldFactory.build(client, () -> refreshGrid(grid))
        );
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

    private void registerUpdateHandler(Grid<Client> grid) {
        eventSubscriberRegistry.addSubscriber(
                GeneralUpdateEvent.class,
                new UpdateHandler(UI.getCurrent(), () -> refreshGrid(grid))
        );
    }

    private void refreshGrid(Grid<Client> grid) {
        grid.setItems(
                Client.from(clientService.getAll())
        );
        log.debug("Grid refreshed on UI = {}", UiUtility.getFullUiId());
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class UpdateHandler implements EventSubscriber<GeneralUpdateEvent> {

        private final UI ui;
        private final Runnable onUpdatedListener;

        @Override
        public void receive(GeneralUpdateEvent event) {
            try {
                ui.access(onUpdatedListener::run);
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(GeneralUpdateEvent.class, this);
            }
        }
    }
}
