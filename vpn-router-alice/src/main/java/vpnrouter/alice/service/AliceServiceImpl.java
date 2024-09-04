package vpnrouter.alice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;

@Slf4j
@Service
@RequiredArgsConstructor
public class AliceServiceImpl implements AliceService {

    private final ClientService clientService;

    @Override
    public void switchTunnelingOnFor(String query) {
        var client = findOneClient(query);
        switchTunneling(client, true);
    }

    @Override
    public void switchTunnelingOffFor(String query) {
        var client = findOneClient(query);
        switchTunneling(client, false);
    }

    private ClientView findOneClient(String query) {
        var clients = clientService.getAll().stream()
                .filter(client -> matches(client, query))
                .toList();
        log.info("For query = '{}', found clients: {}", query, clients);
        if (clients.isEmpty()) {
            throw new IllegalArgumentException("Клиент не найден");
        }
        if (clients.size() > 1) {
            throw new IllegalArgumentException("Подходит больше одного клиента");
        }
        return clients.getFirst();
    }

    private boolean matches(ClientView client, String query) {
        var nameMatches = client.getName() != null && client.getName().equalsIgnoreCase(query);
        var ipAddressMatches = client.getIpAddress().endsWith("." + query);
        return nameMatches || ipAddressMatches;
    }

    private void switchTunneling(ClientView client, boolean tunnelled) {
        var clientUpdate = ClientUpdate.builder()
                .name(client.getName())
                .tunnelled(tunnelled)
                .build();
        clientService.update(client.getIpAddress(), clientUpdate);
    }
}
