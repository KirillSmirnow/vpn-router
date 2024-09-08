package vpnrouter.alice.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Data
@ConfigurationProperties("alice")
public class AliceProperties {
    private final String secret;
    private final String skill;
    private final String user;
    private final Set<String> applications;
}
