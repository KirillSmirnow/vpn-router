package vpnrouter.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import vpnrouter.api.client.ClientService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MainPage {

    private final ClientService clientService;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        log.info("Web started");
    }
}
