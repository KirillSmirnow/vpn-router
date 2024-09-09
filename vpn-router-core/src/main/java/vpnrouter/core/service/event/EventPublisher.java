package vpnrouter.core.service.event;

import vpnrouter.api.event.Event;

public interface EventPublisher {

    <E extends Event> void publish(E event);
}
