package vpnrouter.web.ui.clients.location.updater;

import com.vaadin.flow.component.UI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vpnrouter.api.location.LocationService;
import vpnrouter.web.ui.clients.location.LocationComponent;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationUpdaterImpl implements LocationUpdater {
    private final LocationService locationService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<UI, Set<Future<?>>> scheduledTasksByUis = new ConcurrentHashMap<>();

    @Override
    public void startScheduledUpdates(UI ui, LocationComponent locationComponent) {
        var newScheduledTask = scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        ui.access(() -> updateLocation(ui, locationComponent));
                    } catch (Exception e) {
                        log.debug("Exception while updating location", e);
                        scheduledTasksByUis.get(ui).forEach(unusedTask -> unusedTask.cancel(false));
                        scheduledTasksByUis.remove(ui);
                    }
                },
                0,
                5,
                TimeUnit.SECONDS
        );
        scheduledTasksByUis.computeIfAbsent(ui, newUi -> ConcurrentHashMap.newKeySet()).add(newScheduledTask);
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
            locationComponent.setState(ipAddress, location);
            log.info("Location has been updated by the schedule");
        });
    }
}
