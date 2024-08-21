package vpnrouter.core.service.client.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class FakeTunnelling implements Tunnelling {

    @Override
    public void switchOnOnlyFor(Set<String> ipAddresses) {
        log.info("Switching tunnelling on for {}", ipAddresses);
    }
}
