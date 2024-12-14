package vpnrouter.web.utility;

import com.vaadin.flow.component.UI;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class UiUtility {

    public static String getFullUiId() {
        var ui = UI.getCurrent();
        var uiId = ui.getUIId();
        var sessionId = ui.getSession().getSession().getId();
        return "%s/%s".formatted(sessionId, uiId);
    }

    public static Optional<String> getCurrentClientIpAddress() {
        var address = Optional.ofNullable(
                UI.getCurrent().getSession().getBrowser().getAddress()
        );
        log.info("Current client IP address: {}", address);
        return address;
    }
}
