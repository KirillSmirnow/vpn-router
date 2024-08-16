package vpnrouter.core.service.client.converter;

import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientView;
import vpnrouter.core.service.client.Client;

@Component
public class ClientConverter {

    public ClientView toView(Client client) {
        return ClientView.builder()
                .ipAddress(client.getIpAddress())
                .name(client.getName())
                .tunnelled(client.isTunnelled())
                .build();
    }
}
