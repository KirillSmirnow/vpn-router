package vpnrouter.api.event.concrete.client;

import lombok.Data;
import vpnrouter.api.event.Event;

@Data
public class ClientDetectionClientsFoundEvent implements Event {
    private final int newClientsCount;
}
