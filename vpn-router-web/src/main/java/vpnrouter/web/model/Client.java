package vpnrouter.web.model;

import lombok.*;
import vpnrouter.api.client.ClientCreation;
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

    @Getter
    @Setter
    public static class ClientWrapper {
        private String ipAddress;
        private String name;
        private boolean tunnelled;

        public Client build() {
            return new Client(ipAddress, name, tunnelled);
        }
    }

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

    public ClientCreation toClientCreation() {
        return ClientCreation.builder()
                .ipAddress(ipAddress)
                .name(name)
                .tunnelled(tunnelled)
                .build();
    }
}
