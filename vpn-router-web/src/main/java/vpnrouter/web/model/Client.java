package vpnrouter.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;

import java.util.List;

@Data
@EqualsAndHashCode(of = "ipAddress")
@Builder
public class Client {

    private final String ipAddress;
    @With
    private final String name;
    @With
    private final boolean tunnelled;

    public static Client from(ClientView client) {
        return Client.builder()
                .ipAddress(client.getIpAddress())
                .name(client.getName())
                .tunnelled(client.isTunnelled())
                .build();
    }

    public static List<Client> from(List<ClientView> clients) {
        return clients.stream()
                .map(Client::from)
                .toList();
    }

    public ClientUpdate toClientUpdate() {
        return ClientUpdate.builder()
                .name(name)
                .tunnelled(tunnelled)
                .build();
    }
}
