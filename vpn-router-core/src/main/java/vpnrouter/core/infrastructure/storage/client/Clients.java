package vpnrouter.core.infrastructure.storage.client;

import lombok.Builder;
import lombok.Data;
import vpnrouter.core.service.client.Client;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Clients {
    private final List<Client> clients = new ArrayList<>();
}
