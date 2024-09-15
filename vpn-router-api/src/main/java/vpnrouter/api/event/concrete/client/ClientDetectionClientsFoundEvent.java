package vpnrouter.api.event.concrete.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vpnrouter.api.event.Event;

@AllArgsConstructor
@Getter
public class ClientDetectionClientsFoundEvent implements Event {
    private int newClientsCount;
}
