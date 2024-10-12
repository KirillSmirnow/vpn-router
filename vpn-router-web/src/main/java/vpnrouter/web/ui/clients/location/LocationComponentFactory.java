package vpnrouter.web.ui.clients.location;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vpnrouter.api.event.EventSubscriber;
import vpnrouter.api.event.EventSubscriberRegistry;
import vpnrouter.api.event.concrete.GeneralUpdateEvent;
import vpnrouter.api.location.LocationService;
import vpnrouter.core.service.event.annotation.EventOnSuccess;

import java.util.concurrent.CompletableFuture;


@Component
@RequiredArgsConstructor
public class LocationComponentFactory {

    private final LocationService locationService;
    private final EventSubscriberRegistry eventSubscriberRegistry;

    @EventOnSuccess(GeneralUpdateEvent.class)
    public LocationComponent build(String ipAddress) {
        String location = locationService.getLocation(ipAddress).orElse("");
        LocationComponent locationComponent = new LocationComponent(ipAddress, location);
        eventSubscriberRegistry.addSubscriber(
                GeneralUpdateEvent.class,
                new GeneralUpdateEventHandler(UI.getCurrent(), locationComponent)
        );
        return locationComponent;
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class GeneralUpdateEventHandler implements EventSubscriber<GeneralUpdateEvent> {

        private final UI ui;
        private final LocationComponent locationComponent;

        @Override
        public void receive(GeneralUpdateEvent event) {
            try {
                ui.access(() -> fetchIpAddress().thenAccept(ipAddress -> {
                            String location = locationService.getLocation(ipAddress).orElseThrow();
                            locationComponent.setState(ipAddress, location);
                        })
                );
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(GeneralUpdateEvent.class, this);
            }
        }

        private CompletableFuture<String> fetchIpAddress() {
            var completableFuture = new CompletableFuture<String>();
            ui.getPage().executeJs("""
                          var request = new XMLHttpRequest();
                          request.open("GET", "http://ip.cucurum.ru", false); // false for synchronous request
                          request.send(null);
                          return request.responseText;
                    """).then(result -> completableFuture.complete(result.asString()));
            return completableFuture;
        }
    }
}
