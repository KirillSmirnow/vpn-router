package vpnrouter.web.ui.clients;

import com.vaadin.componentfactory.ToggleButton;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.web.model.ClientWebView;

@Component
@RequiredArgsConstructor
public class ClientTunnelledSwitch {

    private final ClientService clientService;

    public ToggleButton buildSwitch(ClientWebView client, Runnable onSuccessListener) {
        var toggle = new ToggleButton();
        toggle.setValue(client.isTunnelled());
        toggle.addValueChangeListener(event -> {
            var clientUpdate = ClientUpdate.builder()
                    .name(client.getName())
                    .tunnelled(event.getValue())
                    .build();
            clientService.update(client.getIpAddress(), clientUpdate);
            onSuccessListener.run();
        });
        return toggle;
    }
}
