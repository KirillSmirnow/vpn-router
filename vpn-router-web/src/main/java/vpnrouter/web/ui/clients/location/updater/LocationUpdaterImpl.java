package vpnrouter.web.ui.clients.location.updater;

import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vpnrouter.api.location.LocationService;
import vpnrouter.web.ui.clients.location.LocationComponent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationUpdaterImpl implements LocationUpdater {
    private final LocationService locationService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void startScheduledUpdates(UI ui, LocationComponent locationComponent) {
        scheduler.scheduleAtFixedRate(
                () -> ui.access(() -> updateLocation(ui, locationComponent)),
                0,
                5,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void updateLocation(UI ui, LocationComponent locationComponent) {
        ui.getPage().executeJs("""
                var request = new XMLHttpRequest();
                request.open("GET", "http://ip.cucurum.ru", false); // false for synchronous request
                request.send(null);
                return request.responseText;
                """).then(result -> {
            var ipAddress = result.asString();
            var location = locationService.getLocation(ipAddress).orElse("N/A");
            log.info("Location has been updated by the schedule");
            locationComponent.setState(ipAddress, location);
        });
    }
}
