package vpnrouter.web.ui.clients;

import com.vaadin.componentfactory.ToggleButton;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.web.model.Client;

@Component
@RequiredArgsConstructor
public class ClientTunnelSwitchFactory {

    private final ClientService clientService;

    public ToggleButton build(Client client, Runnable onSwitchedListener) {
        var toggle = new ToggleButton();
        toggle.setValue(client.isTunnelled());
        toggle.addValueChangeListener(event -> {
            var clientUpdate = client.withTunnelled(event.getValue()).toClientUpdate();
            clientService.update(client.getIpAddress(), clientUpdate);
            onSwitchedListener.run();
        });
        return toggle;
    }
}
