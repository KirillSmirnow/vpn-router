package vpnrouter.api.event;

public interface EventSubscriberRegistry {

    <E extends Event> void addSubscriber(Class<E> eventType, EventSubscriber<E> eventSubscriber);
}
