package vpnrouter.api.event.concrete.client;

import lombok.Data;
import vpnrouter.api.event.Event;

@Data
public class ClientDetectionFailureEvent implements Event {
    private final Exception exception;
}
