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

@Component
@RequiredArgsConstructor
public class LocationComponentFactory {

    private final LocationService locationService;
    private final EventSubscriberRegistry eventSubscriberRegistry;

    public LocationComponent build() {
        var locationComponent = new LocationComponent();
        updateLocation(UI.getCurrent(), locationComponent);
        eventSubscriberRegistry.addSubscriber(
                GeneralUpdateEvent.class,
                new GeneralUpdateEventHandler(UI.getCurrent(), locationComponent)
        );
        return locationComponent;
    }

    private void updateLocation(UI ui, LocationComponent locationComponent) {
        ui.getPage().executeJs("""
                var request = new XMLHttpRequest();
                request.open("GET", "http://ip.cucurum.ru", false); // false for synchronous request
                request.send(null);
                return request.responseText;
                """).then(result -> {
            var ipAddress = result.asString();
            var location = locationService.getLocation(ipAddress).orElse("N/A");
            locationComponent.setState(ipAddress, location);
        });
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(of = "ui")
    private class GeneralUpdateEventHandler implements EventSubscriber<GeneralUpdateEvent> {

        private final UI ui;
        private final LocationComponent locationComponent;

        @Override
        public void receive(GeneralUpdateEvent event) {
            try {
                ui.access(() -> updateLocation(ui, locationComponent));
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(GeneralUpdateEvent.class, this);
            }
        }
    }
}
