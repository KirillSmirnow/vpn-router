package vpnrouter.core.infrastructure.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import vpnrouter.core.service.client.client.Tunnelling;

import java.util.Set;

import static java.util.stream.Collectors.joining;

@Primary
@Component
@Profile("prod")
@RequiredArgsConstructor
public class RealTunnelling implements Tunnelling {

    private static final int TABLE = 7373;

    private final NetworkProperties networkProperties;
    private final ShellExecutor shellExecutor;

    @Override
    public void switchOnOnlyFor(Set<String> ipAddresses) {
        shellExecutor.execute(
                buildResetTableCommand() + buildSwitchOffCommand() + buildSwitchOnCommand(ipAddresses)
        );
    }

    private String buildResetTableCommand() {
        var cleanCommand = "ip route flush table %d\n".formatted(TABLE);
        var initializeCommand = "ip route add default dev %s table %d\n".formatted(networkProperties.getVpnInterface(), TABLE);
        return cleanCommand + initializeCommand;
    }

    private String buildSwitchOffCommand() {
        return "while ip rule delete table %d; do echo 'removed'; done\n".formatted(TABLE);
    }

    private String buildSwitchOnCommand(Set<String> ipAddresses) {
        return ipAddresses.stream()
                .map(ipAddress -> "ip rule add from %s table %s\n".formatted(ipAddress, TABLE))
                .collect(joining());
    }
}
