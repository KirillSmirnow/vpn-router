package vpnrouter.core.service.client.detection;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vpnrouter.api.client.ClientDetectionService;
import vpnrouter.core.service.client.Client;
import vpnrouter.core.service.client.ClientRepository;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class ClientDetectionServiceImpl implements ClientDetectionService {

    private final ClientRepository clientRepository;
    private final ClientDetector clientDetector;

    @Override
    public void detectAndSave() {
        var detectedIpAddresses = clientDetector.detectIpAddresses();
        var existingIpAddresses = clientRepository.findAll().stream()
                .map(Client::getIpAddress)
                .collect(toSet());
        for (var detectedIpAddress : detectedIpAddresses) {
            if (!existingIpAddresses.contains(detectedIpAddress)) {
                addClient(detectedIpAddress);
            }
        }
    }

    private void addClient(String ipAddress) {
        var client = Client.builder()
                .ipAddress(ipAddress)
                .lastSwitchedAt(LocalDateTime.now())
                .build();
        clientRepository.save(client);
    }
}
