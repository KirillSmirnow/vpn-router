package vpnrouter.web.utility;

import com.vaadin.flow.component.UI;

public class UiUtility {

    public static String getFullUiId() {
        var ui = UI.getCurrent();
        var uiId = ui.getUIId();
        var sessionId = ui.getSession().getSession().getId();
        return "%s/%s".formatted(sessionId, uiId);
    }
}
