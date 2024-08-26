package vpnrouter.core.infrastructure.shell;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("network")
public class NetworkProperties {
    private final String address;
}
