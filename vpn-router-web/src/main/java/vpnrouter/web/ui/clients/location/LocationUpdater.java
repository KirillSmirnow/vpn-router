package vpnrouter.web.ui.clients.location;

import com.vaadin.flow.component.UI;
import lombok.extern.slf4j.Slf4j;
import vpnrouter.api.location.LocationService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocationUpdater {
    private final LocationService locationService;
    private final ScheduledExecutorService scheduler;

    public LocationUpdater(LocationService locationService) {
        this.locationService = locationService;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startScheduledUpdates(UI ui, LocationComponent locationComponent) {
        scheduler.scheduleAtFixedRate(() -> {
            ui.access(() -> updateLocation(ui, locationComponent));
            log.info("Location has been updated according to the schedule");
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void updateLocation(UI ui, LocationComponent locationComponent) {
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
}
