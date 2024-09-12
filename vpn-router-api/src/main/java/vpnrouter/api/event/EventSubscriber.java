package vpnrouter.api.event;

public interface EventSubscriber<E extends Event> {

    void receive(E event);
}
