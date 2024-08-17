package vpnrouter.core.service.client.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vpnrouter.api.client.ClientCreation;
import vpnrouter.api.client.ClientService;
import vpnrouter.api.client.ClientUpdate;
import vpnrouter.api.client.ClientView;
import vpnrouter.core.service.EntityConverter;
import vpnrouter.core.service.client.Client;
import vpnrouter.core.service.client.ClientRepository;

import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final EntityConverter<Client, ClientView> clientConverter;

    @Override
    public List<ClientView> getAll() {
        log.info("Getting all clients");
        return emptyList();
    }

    @Override
    public void add(ClientCreation creation) {
        log.info("Adding client: {}", creation);
    }

    @Override
    public void update(String ipAddress, ClientUpdate update) {
        log.info("Updating client {}: {}", ipAddress, update);
    }

    @Override
    public void remove(String ipAddress) {
        log.info("Removing client {}", ipAddress);
    }
}
