package vpnrouter.alice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Data
@ConfigurationProperties("alice")
public class AliceProperties {
    private final String skillId;
    private final String userId;
    private final Set<String> applicationIds;
}
