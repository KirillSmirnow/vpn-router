package vpnrouter.core.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientDetectionService;

@Component
@RequiredArgsConstructor
public class ClientDetectionScheduler {

    private final ClientDetectionService clientDetectionService;

    @Scheduled(fixedRateString = "PT30m")
    public void detectAndSave() {
        clientDetectionService.detectAndSave();
    }
}
