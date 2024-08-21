package vpnrouter.api.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientView {
    private final String ipAddress;
    private final String name;
    private final boolean tunnelled;
}
