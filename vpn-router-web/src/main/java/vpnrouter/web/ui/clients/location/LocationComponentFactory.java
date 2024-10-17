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
    private LocationUpdater locationUpdater;

    public LocationComponent build() {
        var locationComponent = new LocationComponent();
        this.locationUpdater = new LocationUpdater(locationService);
        locationUpdater.startScheduledTask(UI.getCurrent(), locationComponent);
        locationUpdater.updateLocation(UI.getCurrent(), locationComponent);
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
                ui.access(() -> locationUpdater.updateLocation(ui, locationComponent));
            } catch (UIDetachedException e) {
                eventSubscriberRegistry.removeSubscriber(GeneralUpdateEvent.class, this);
            }
        }
    }
}
