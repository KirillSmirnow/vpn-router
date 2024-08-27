package vpnrouter.core.infrastructure.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import vpnrouter.core.service.client.client.Tunnelling;

import java.util.Set;

@Primary
@Component
@Profile("prod")
@RequiredArgsConstructor
public class RealTunnelling implements Tunnelling {

    private static final int FIRST_TABLE = 1000;

    private final int maxClients = 100;
    private final String vpnInterface = "wg";

    private final String switchOffCommand = buildSwitchOffCommand();

    private String buildSwitchOffCommand() {
        var builder = new StringBuilder();
        for (var table = FIRST_TABLE; table < FIRST_TABLE + maxClients; table++) {
            builder.append("ip rule del lookup %s\n".formatted(table));
        }
        return builder.toString();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void test() {
        switchOnOnlyFor(Set.of("192.168.0.100", "192.168.0.50"));
    }

    @Override
    public void switchOnOnlyFor(Set<String> ipAddresses) {
        var switchOnCommand = buildSwitchOnCommand(ipAddresses);
        System.out.println(switchOffCommand + switchOnCommand);
    }

    private String buildSwitchOnCommand(Set<String> ipAddresses) {
        var builder = new StringBuilder();
        var table = FIRST_TABLE;
        for (var ipAddress : ipAddresses) {
            builder.append("ip rule add from %s lookup %s\n".formatted(ipAddress, table));
            builder.append("ip route add default via %s table %s\n".formatted(vpnInterface, table));
            table++;
        }
        return builder.toString();
    }
}
