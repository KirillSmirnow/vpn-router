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
        clientDetectionService.detectAndSave(new ClientDetectionService.CompletionListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onAlreadyRunning() {
            }

            @Override
            public void onNewClientsNotFound() {
            }

            @Override
            public void onNewClientsFound(int newClientsCount) {
            }

            @Override
            public void onFailure(Exception exception) {
            }
        });
    }
}
