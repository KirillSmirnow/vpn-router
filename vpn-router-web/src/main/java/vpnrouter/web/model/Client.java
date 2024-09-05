package vpnrouter.web.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import vpnrouter.api.client.ClientCreation;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;

import java.util.List;

@Data
@EqualsAndHashCode(of = "ipAddress")
public class Client {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

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

    public static List<Client> from(List<ClientView> clients) {
        return clients.stream()
                .map(Client::from)
                .toList();
    }

    public static Client from(ClientView client) {
        return OBJECT_MAPPER.convertValue(client, Client.class);
    }

    public ClientUpdate toClientUpdate() {
        return OBJECT_MAPPER.convertValue(this, ClientUpdate.class);
    }

    public ClientCreation toClientCreation() {
        return ClientCreation.builder()
                .ipAddress(ipAddress)
                .name(name)
                .tunnelled(tunnelled)
                .build();
    }
}
