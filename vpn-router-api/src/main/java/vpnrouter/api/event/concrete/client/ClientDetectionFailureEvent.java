package vpnrouter.api.event.concrete.client;

import lombok.AllArgsConstructor;
import vpnrouter.api.event.Event;

@AllArgsConstructor
public class ClientDetectionFailureEvent implements Event {
    private Exception exception;
}
