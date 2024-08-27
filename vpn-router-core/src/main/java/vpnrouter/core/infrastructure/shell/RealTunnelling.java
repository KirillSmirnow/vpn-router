package vpnrouter.core.infrastructure.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import vpnrouter.core.service.client.client.Tunnelling;

import java.util.Set;

@Primary
@Component
@Profile("prod")
@RequiredArgsConstructor
public class RealTunnelling implements Tunnelling {

    private static final int FIRST_TABLE = 1000;

    private final NetworkProperties networkProperties;
    private final ShellExecutor shellExecutor;

    @Override
    public void switchOnOnlyFor(Set<String> ipAddresses) {
        shellExecutor.execute(
                buildSwitchOffCommand() + buildSwitchOnCommand(ipAddresses)
        );
    }

    private String buildSwitchOffCommand() {
        var builder = new StringBuilder();
        var maxClients = networkProperties.getMaxClients();
        for (var table = FIRST_TABLE; table < FIRST_TABLE + maxClients; table++) {
            builder.append("ip rule del lookup %s\n".formatted(table));
        }
        return builder.toString();
    }

    private String buildSwitchOnCommand(Set<String> ipAddresses) {
        var builder = new StringBuilder();
        var vpnInterface = networkProperties.getVpnInterface();
        var table = FIRST_TABLE;
        for (var ipAddress : ipAddresses) {
            builder.append("ip rule add from %s lookup %s\n".formatted(ipAddress, table));
            builder.append("ip route add default via %s table %s\n".formatted(vpnInterface, table));
            table++;
        }
        return builder.toString();
    }
}
