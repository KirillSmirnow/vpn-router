package vpnrouter.web;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import org.springframework.stereotype.Component;

@Component
@Push
public class WebApplication implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.setPageTitle("VPN Router");
        settings.addFavIcon("icon", "router.png", "");
    }
}
