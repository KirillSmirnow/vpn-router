package vpnrouter.core.infrastructure.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import vpnrouter.core.service.client.detection.ClientDetector;

import java.util.List;

@Primary
@Component
@Profile("prod")
@RequiredArgsConstructor
public class RealClientDetector implements ClientDetector {

    private final NetworkProperties networkProperties;
    private final ShellExecutor shellExecutor;

    @Override
    public List<String> detectIpAddresses() {
        return shellExecutor.execute("nmap -sn -n %s -oG - | awk '/Host/{print $2}'".formatted(
                networkProperties.getAddress()
        ));
    }
}
