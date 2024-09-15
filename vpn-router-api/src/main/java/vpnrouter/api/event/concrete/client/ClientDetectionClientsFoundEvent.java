package vpnrouter.api.event.concrete.client;

import lombok.AllArgsConstructor;
import vpnrouter.api.event.Event;

@AllArgsConstructor
public class ClientDetectionClientsFoundEvent implements Event {
    private int newClientsCount;
}
