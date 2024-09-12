package vpnrouter.core.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vpnrouter.api.event.Event;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
public class EventManager implements EventSubscriberRegistry, EventPublisher {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Map<
            Class<? extends Event>,
            Set<EventSubscriber<? extends Event>>
            > subscribers = new ConcurrentHashMap<>();

    @Override
    public <E extends Event> void addSubscriber(Class<E> eventType, EventSubscriber<E> eventSubscriber) {
        subscribers.computeIfAbsent(eventType, $ -> ConcurrentHashMap.newKeySet())
                .add(eventSubscriber);
        logSubscribers();
    }

    @Override
    public <E extends Event> void removeSubscriber(Class<E> eventType, EventSubscriber<E> eventSubscriber) {
        subscribers.getOrDefault(eventType, emptySet())
                .remove(eventSubscriber);
        logSubscribers();
    }

    private void logSubscribers() {
        var description = subscribers.entrySet().stream()
                .map(entry -> "%s -> %d".formatted(entry.getKey().getSimpleName(), entry.getValue().size()))
                .sorted()
                .collect(joining(", "));
        log.info("Event subscribers: [{}]", description);
    }

    @Override
    public <E extends Event> void publish(E event) {
        getSubscribers(event).forEach(subscriber ->
                executor.submit(() -> {
                    try {
                        subscriber.receive(event);
                    } catch (Exception e) {
                        log.warn("Error during event handling: {}", e.getMessage(), e);
                    }
                })
        );
    }

    @SuppressWarnings("unchecked")
    private <E extends Event> Set<EventSubscriber<? super E>> getSubscribers(E event) {
        return subscribers.getOrDefault(event.getClass(), emptySet()).stream()
                .map(subscribers -> (EventSubscriber<? super E>) subscribers)
                .collect(toSet());
    }
}
