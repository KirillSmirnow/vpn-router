package vpnrouter.web.ui.clients.location.updater;

import com.vaadin.flow.component.UI;
import vpnrouter.web.ui.clients.location.LocationComponent;

public interface LocationUpdater {
    void updateLocation(UI ui, LocationComponent locationComponent);

    void startScheduledUpdates(UI ui, LocationComponent locationComponent);
}
