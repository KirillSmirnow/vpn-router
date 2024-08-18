package vpnrouter.core.service.client.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vpnrouter.api.client.ClientCreation;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;
import vpnrouter.core.exception.UserException;
import vpnrouter.core.service.EntityConverter;
import vpnrouter.core.service.client.Client;
import vpnrouter.core.service.client.ClientRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final EntityConverter<Client, ClientView> clientConverter;
    private final Tunnelling tunnelling;

    @Override
    public List<ClientView> getAll() {
        return clientRepository.findAll().stream()
                .map(clientConverter::toView)
                .toList();
    }

    @Override
    public void add(ClientCreation creation) {
        if (clientRepository.find(creation.getIpAddress()).isPresent()) {
            throw new UserException("Client already exists");
        }
        var client = Client.builder()
                .ipAddress(creation.getIpAddress())
                .name(creation.getName())
                .tunnelled(creation.isTunnelled())
                .lastSwitchedAt(LocalDateTime.now())
                .build();
        switchTunnelFor(client);
        clientRepository.save(client);
    }

    @Override
    public void update(String ipAddress, ClientUpdate update) {
        var client = clientRepository.find(ipAddress)
                .orElseThrow(() -> new UserException("Client not found"));
        client.setName(update.getName());
        if (client.isTunnelled() != update.isTunnelled()) {
            client.setTunnelled(update.isTunnelled());
            client.setLastSwitchedAt(LocalDateTime.now());
            switchTunnelFor(client);
        }
        clientRepository.save(client);
    }

    @Override
    public void remove(String ipAddress) {
        var client = clientRepository.find(ipAddress)
                .orElseThrow(() -> new UserException("Client not found"));
        client.setTunnelled(false);
        switchTunnelFor(client);
        clientRepository.deleteIfExists(ipAddress);
    }

    private void switchTunnelFor(Client client) {
        var tunnelledIpAddresses = clientRepository.findAll().stream()
                .filter(Client::isTunnelled)
                .map(Client::getIpAddress)
                .collect(toSet());
        if (client.isTunnelled()) {
            tunnelledIpAddresses.add(client.getIpAddress());
        } else {
            tunnelledIpAddresses.remove(client.getIpAddress());
        }
        tunnelling.switchOnOnlyFor(tunnelledIpAddresses);
    }
}
