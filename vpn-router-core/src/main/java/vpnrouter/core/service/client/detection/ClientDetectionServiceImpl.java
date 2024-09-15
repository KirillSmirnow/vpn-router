package vpnrouter.core.service.client.detection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vpnrouter.api.client.ClientDetectionService;
import vpnrouter.api.event.concrete.*;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionClientsNotFoundEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionFailureEvent;
import vpnrouter.api.event.concrete.client.ClientDetectionStartedEvent;
import vpnrouter.core.service.client.Client;
import vpnrouter.core.service.client.ClientRepository;
import vpnrouter.core.service.event.EventPublisher;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientDetectionServiceImpl implements ClientDetectionService {

    private final AtomicBoolean detectionInProgress = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ClientRepository clientRepository;
    private final ClientDetector clientDetector;
    private final EventPublisher eventPublisher;

    @Override
    public void detectAndSave() {
        if (detectionInProgress.compareAndSet(false, true)) {
            executor.submit(() -> {
                try {
                    executeTask();
                } catch (Exception e) {
                    log.warn("Detection failure: {}", e.getMessage(), e);
                    eventPublisher.publish(new ClientDetectionFailureEvent(e));
                } finally {
                    detectionInProgress.set(false);
                }
            });
            log.info("Detection task submitted");
        } else {
            log.info("Detection already running");
        }
    }

    private void executeTask() {
        eventPublisher.publish(new ClientDetectionStartedEvent());
        log.info("Detecting and saving clients");
        var detectedIpAddresses = clientDetector.detectIpAddresses();
        var existingIpAddresses = clientRepository.findAll().stream()
                .map(Client::getIpAddress)
                .collect(toSet());
        var newClientsCount = 0;
        for (var detectedIpAddress : detectedIpAddresses) {
            if (!existingIpAddresses.contains(detectedIpAddress)) {
                addClient(detectedIpAddress);
                newClientsCount++;
            }
        }
        if (newClientsCount == 0) {
            log.info("Detection completed: new clients not found");
            eventPublisher.publish(new ClientDetectionClientsNotFoundEvent());
        } else {
            log.info("Detection completed: {} new clients found", newClientsCount);
            eventPublisher.publish(new ClientDetectionClientsFoundEvent(newClientsCount));
            eventPublisher.publish(new GeneralUpdateEvent());
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
